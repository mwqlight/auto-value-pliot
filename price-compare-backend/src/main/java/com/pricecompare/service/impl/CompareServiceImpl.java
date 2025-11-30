package com.pricecompare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pricecompare.entity.CompareTask;
import com.pricecompare.entity.Product;
import com.pricecompare.entity.ProductPrice;
import com.pricecompare.mapper.CompareTaskMapper;
import com.pricecompare.mapper.ProductPriceMapper;
import com.pricecompare.service.CompareService;
import com.pricecompare.service.CrawlerService;
import com.pricecompare.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 比价服务实现类
 * 
 * @author AutoValuePilot
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CompareServiceImpl extends ServiceImpl<CompareTaskMapper, CompareTask> implements CompareService {

    private final ProductService productService;
    private final CrawlerService crawlerService;
    private final ProductPriceMapper productPriceMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public CompareTask startCompareTask(String productName) {
        // 创建比价任务
        CompareTask task = new CompareTask();
        task.setProductName(productName);
        task.setStatus(1); // 处理中
        task.setStartTime(LocalDateTime.now());
        this.save(task);

        // 异步执行比价任务
        executeCompareTaskAsync(task.getId(), productName);

        return task;
    }

    @Async
    public void executeCompareTaskAsync(Long taskId, String productName) {
        try {
            log.info("开始执行比价任务: {}, 商品名称: {}", taskId, productName);
            
            // 获取支持的平台列表
            List<String> supportedPlatforms = crawlerService.getSupportedPlatforms();
            log.info("支持的平台: {}", supportedPlatforms);
            
            // 使用爬虫服务搜索商品
            List<ProductPrice> searchResults = crawlerService.batchSearchProducts(productName, supportedPlatforms);
            log.info("搜索到 {} 个商品", searchResults.size());
            
            // 保存商品价格信息到数据库
            if (!searchResults.isEmpty()) {
                for (ProductPrice price : searchResults) {
                    price.setCreateTime(LocalDateTime.now());
                    price.setUpdateTime(LocalDateTime.now());
                    productPriceMapper.insert(price);
                }
                log.info("保存了 {} 个商品价格记录", searchResults.size());
            }
            
            // 更新任务状态为完成
            CompareTask task = this.getById(taskId);
            if (task != null) {
                task.setStatus(2); // 已完成
                task.setEndTime(LocalDateTime.now());
                task.setResultCount(searchResults.size());
                this.updateById(task);
                
                log.info("比价任务完成: {}, 找到 {} 个结果", taskId, searchResults.size());
            }
        } catch (Exception e) {
            log.error("比价任务执行失败: {}", taskId, e);
            
            // 更新任务状态为失败
            CompareTask task = this.getById(taskId);
            if (task != null) {
                task.setStatus(3); // 失败
                task.setEndTime(LocalDateTime.now());
                task.setErrorMessage(e.getMessage());
                this.updateById(task);
            }
        }
    }

    @Override
    public List<CompareTask> getCompareTasks(Integer page, Integer pageSize) {
        String cacheKey = "compare:tasks:" + page + ":" + pageSize;
        
        // 尝试从缓存获取
        List<CompareTask> cachedTasks = (List<CompareTask>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedTasks != null) {
            return cachedTasks;
        }

        // 数据库查询
        LambdaQueryWrapper<CompareTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(CompareTask::getCreateTime);

        Page<CompareTask> taskPage = new Page<>(page, pageSize);
        Page<CompareTask> result = this.page(taskPage, queryWrapper);
        
        List<CompareTask> tasks = result.getRecords();
        
        // 缓存结果，有效期10分钟
        redisTemplate.opsForValue().set(cacheKey, tasks, 10, TimeUnit.MINUTES);
        
        return tasks;
    }

    @Override
    public CompareTask getCompareTaskById(Long taskId) {
        String cacheKey = "compare:task:" + taskId;
        
        // 尝试从缓存获取
        CompareTask cachedTask = (CompareTask) redisTemplate.opsForValue().get(cacheKey);
        if (cachedTask != null) {
            return cachedTask;
        }

        // 数据库查询
        CompareTask task = this.getById(taskId);
        
        if (task != null) {
            // 缓存任务信息，有效期30分钟
            redisTemplate.opsForValue().set(cacheKey, task, 30, TimeUnit.MINUTES);
        }
        
        return task;
    }

    @Override
    public List<Object> getCompareResults(Long taskId) {
        String cacheKey = "compare:results:" + taskId;
        
        // 尝试从缓存获取
        List<Object> cachedResults = (List<Object>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResults != null) {
            return cachedResults;
        }

        // 获取比价任务
        CompareTask task = this.getById(taskId);
        if (task == null) {
            throw new RuntimeException("比价任务不存在: " + taskId);
        }

        // 从数据库获取比价结果
        LambdaQueryWrapper<ProductPrice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(ProductPrice::getPrice); // 按价格升序排列
        List<ProductPrice> productPrices = productPriceMapper.selectList(queryWrapper);
        
        // 找到最低价格
        BigDecimal lowestPrice = productPrices.stream()
                .map(ProductPrice::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        
        // 转换为比价结果格式
        List<Object> results = productPrices.stream()
                .map(price -> {
                    // 标记是否为最低价
                    boolean isLowest = price.getPrice().compareTo(lowestPrice) == 0;
                    
                    // 创建Map来存储结果，避免匿名内部类的字段访问问题
                    Map<String, Object> result = new HashMap<>();
                    result.put("id", price.getId());
                    result.put("platformProductId", price.getPlatformProductId());
                    result.put("platform", price.getPlatformCode());
                    result.put("price", price.getPrice());
                    result.put("originalPrice", price.getOriginalPrice());
                    result.put("discount", price.getDiscount());
                    result.put("sales", price.getSales());
                    result.put("rating", price.getRating());
                    result.put("shopName", price.getShopName());
                    result.put("shopRating", price.getShopRating());
                    result.put("delivery", price.getDelivery());
                    result.put("isLowest", isLowest);
                    result.put("crawlTime", price.getCrawlTime());
                    
                    return result;
                })
                .collect(java.util.stream.Collectors.toList());

        // 缓存结果，有效期15分钟
        redisTemplate.opsForValue().set(cacheKey, results, 15, TimeUnit.MINUTES);
        
        return results;
    }

    @Override
    public void deleteCompareTask(Long taskId) {
        // 删除任务
        this.removeById(taskId);
        
        // 清除相关缓存
        clearCompareCache(taskId);
    }

    /**
     * 清除比价相关缓存
     */
    private void clearCompareCache(Long taskId) {
        // 清除任务缓存
        redisTemplate.delete("compare:task:" + taskId);
        
        // 清除结果缓存
        redisTemplate.delete("compare:results:" + taskId);
        
        // 清除任务列表缓存（模糊匹配）
        redisTemplate.keys("compare:tasks:*").forEach(redisTemplate::delete);
    }
}