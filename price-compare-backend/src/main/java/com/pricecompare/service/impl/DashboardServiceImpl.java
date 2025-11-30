package com.pricecompare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pricecompare.entity.CompareTask;
import com.pricecompare.entity.Product;
import com.pricecompare.mapper.CompareTaskMapper;
import com.pricecompare.mapper.ProductMapper;
import com.pricecompare.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 数据可视化服务实现类
 * 
 * @author AutoValuePilot
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ProductMapper productMapper;
    private final CompareTaskMapper compareTaskMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Map<String, Object> getDashboardStats() {
        String cacheKey = "dashboard:stats";
        
        // 尝试从缓存获取
        Map<String, Object> cachedStats = (Map<String, Object>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedStats != null) {
            return cachedStats;
        }

        Map<String, Object> stats = new HashMap<>();

        // 商品总数
        Long totalProducts = productMapper.selectCount(null);
        stats.put("totalProducts", totalProducts);

        // 比价任务总数
        Long totalTasks = compareTaskMapper.selectCount(null);
        stats.put("totalTasks", totalTasks);

        // 今日新增任务
        LambdaQueryWrapper<CompareTask> todayQuery = new LambdaQueryWrapper<>();
        todayQuery.ge(CompareTask::getCreateTime, LocalDateTime.now().toLocalDate().atStartOfDay());
        Long todayTasks = compareTaskMapper.selectCount(todayQuery);
        stats.put("todayTasks", todayTasks);

        // 平台数量统计
        List<Product> products = productMapper.selectList(null);
        long platformCount = products.stream()
                .map(Product::getPlatform)
                .distinct()
                .count();
        stats.put("platformCount", platformCount);

        // 缓存结果，有效期5分钟
        redisTemplate.opsForValue().set(cacheKey, stats, 5, TimeUnit.MINUTES);
        
        return stats;
    }

    @Override
    public Map<String, Long> getPlatformDistribution() {
        String cacheKey = "dashboard:platform:distribution";
        
        // 尝试从缓存获取
        Map<String, Long> cachedDistribution = (Map<String, Long>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedDistribution != null) {
            return cachedDistribution;
        }

        // 获取所有商品
        List<Product> products = productMapper.selectList(null);
        
        // 按平台分组统计
        Map<String, Long> distribution = products.stream()
                .collect(Collectors.groupingBy(
                        Product::getPlatform,
                        Collectors.counting()
                ));

        // 缓存结果，有效期10分钟
        redisTemplate.opsForValue().set(cacheKey, distribution, 10, TimeUnit.MINUTES);
        
        return distribution;
    }

    @Override
    public Map<String, Object> getPriceTrend() {
        String cacheKey = "dashboard:price:trend";
        
        // 尝试从缓存获取
        Map<String, Object> cachedTrend = (Map<String, Object>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedTrend != null) {
            return cachedTrend;
        }

        Map<String, Object> trend = new HashMap<>();

        // 获取商品价格数据
        List<Product> products = productMapper.selectList(null);
        
        // 平均价格
        double avgPrice = products.stream()
                .map(product -> product.getPrice().doubleValue())
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
        trend.put("averagePrice", avgPrice);

        // 最高价格
        double maxPrice = products.stream()
                .map(product -> product.getPrice().doubleValue())
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0);
        trend.put("maxPrice", maxPrice);

        // 最低价格
        double minPrice = products.stream()
                .map(product -> product.getPrice().doubleValue())
                .mapToDouble(Double::doubleValue)
                .min()
                .orElse(0.0);
        trend.put("minPrice", minPrice);

        // 价格分布区间
        Map<String, Long> priceRanges = products.stream()
                .collect(Collectors.groupingBy(
                        product -> {
                            double price = product.getPrice().doubleValue();
                            if (price < 100) return "0-100";
                            else if (price < 500) return "100-500";
                            else if (price < 1000) return "500-1000";
                            else if (price < 5000) return "1000-5000";
                            else return "5000+";
                        },
                        Collectors.counting()
                ));
        trend.put("priceRanges", priceRanges);

        // 缓存结果，有效期15分钟
        redisTemplate.opsForValue().set(cacheKey, trend, 15, TimeUnit.MINUTES);
        
        return trend;
    }

    @Override
    public Map<String, Object> getTaskAnalysis() {
        String cacheKey = "dashboard:task:analysis";
        
        // 尝试从缓存获取
        Map<String, Object> cachedAnalysis = (Map<String, Object>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedAnalysis != null) {
            return cachedAnalysis;
        }

        Map<String, Object> analysis = new HashMap<>();

        // 获取所有任务
        List<CompareTask> tasks = compareTaskMapper.selectList(null);

        // 任务状态统计
        Map<Integer, Long> statusStats = tasks.stream()
                .collect(Collectors.groupingBy(
                        CompareTask::getStatus,
                        Collectors.counting()
                ));
        analysis.put("statusStats", statusStats);

        // 任务完成率
        long completedTasks = tasks.stream()
                .filter(task -> task.getStatus() != null && task.getStatus() == 2) // 2表示已完成
                .count();
        double completionRate = tasks.isEmpty() ? 0.0 : (double) completedTasks / tasks.size() * 100;
        analysis.put("completionRate", completionRate);

        // 平均执行时间（秒）
        double avgExecutionTime = tasks.stream()
                .filter(task -> task.getStartTime() != null && task.getEndTime() != null)
                .mapToLong(task -> java.time.Duration.between(task.getStartTime(), task.getEndTime()).getSeconds())
                .average()
                .orElse(0.0);
        analysis.put("avgExecutionTime", avgExecutionTime);

        // 缓存结果，有效期10分钟
        redisTemplate.opsForValue().set(cacheKey, analysis, 10, TimeUnit.MINUTES);
        
        return analysis;
    }

    @Override
    public Map<String, Long> getHotKeywords() {
        String cacheKey = "dashboard:hot:keywords";
        
        // 尝试从缓存获取
        Map<String, Long> cachedKeywords = (Map<String, Long>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedKeywords != null) {
            return cachedKeywords;
        }

        // 获取所有比价任务
        List<CompareTask> tasks = compareTaskMapper.selectList(null);
        
        // 统计热门关键词（这里简化处理，实际应该从搜索日志中获取）
        Map<String, Long> hotKeywords = tasks.stream()
                .collect(Collectors.groupingBy(
                        CompareTask::getProductName,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        java.util.LinkedHashMap::new
                ));

        // 缓存结果，有效期5分钟
        redisTemplate.opsForValue().set(cacheKey, hotKeywords, 5, TimeUnit.MINUTES);
        
        return hotKeywords;
    }
}