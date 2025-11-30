package com.pricecompare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pricecompare.entity.Product;
import com.pricecompare.mapper.ProductMapper;
import com.pricecompare.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 商品服务实现类
 * 
 * @author AutoValuePilot
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<Product> searchProducts(String keyword, Integer page, Integer pageSize) {
        String cacheKey = "product:search:" + keyword + ":" + page + ":" + pageSize;
        
        // 尝试从缓存获取
        List<Product> cachedProducts = (List<Product>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedProducts != null) {
            log.info("从缓存获取搜索结果: {}", keyword);
            return cachedProducts;
        }

        // 数据库查询
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Product::getTitle, keyword)
                   .or()
                   .like(Product::getBrand, keyword)
                   .or()
                   .like(Product::getModel, keyword)
                   .orderByDesc(Product::getUpdateTime);

        Page<Product> productPage = new Page<>(page, pageSize);
        Page<Product> result = this.page(productPage, queryWrapper);
        
        List<Product> products = result.getRecords();
        
        // 缓存结果，有效期1小时
        redisTemplate.opsForValue().set(cacheKey, products, 1, TimeUnit.HOURS);
        
        return products;
    }

    @Override
    public Product getProductById(Long id) {
        String cacheKey = "product:id:" + id;
        
        // 尝试从缓存获取
        Product cachedProduct = (Product) redisTemplate.opsForValue().get(cacheKey);
        if (cachedProduct != null) {
            return cachedProduct;
        }

        // 数据库查询
        Product product = this.getById(id);
        
        if (product != null) {
            // 缓存商品信息，有效期30分钟
            redisTemplate.opsForValue().set(cacheKey, product, 30, TimeUnit.MINUTES);
        }
        
        return product;
    }

    @Override
    public List<Product> getProductsByPlatform(String platform, Integer page, Integer pageSize) {
        String cacheKey = "product:platform:" + platform + ":" + page + ":" + pageSize;
        
        // 尝试从缓存获取
        List<Product> cachedProducts = (List<Product>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedProducts != null) {
            return cachedProducts;
        }

        // 数据库查询
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getPlatform, platform)
                   .orderByDesc(Product::getUpdateTime);

        Page<Product> productPage = new Page<>(page, pageSize);
        Page<Product> result = this.page(productPage, queryWrapper);
        
        List<Product> products = result.getRecords();
        
        // 缓存结果，有效期30分钟
        redisTemplate.opsForValue().set(cacheKey, products, 30, TimeUnit.MINUTES);
        
        return products;
    }

    @Override
    public List<Product> getHotProducts(Integer limit) {
        String cacheKey = "product:hot:" + limit;
        
        // 尝试从缓存获取
        List<Product> cachedProducts = (List<Product>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedProducts != null) {
            return cachedProducts;
        }

        // 数据库查询 - 获取最近更新的热门商品
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Product::getUpdateTime)
                   .last("LIMIT " + limit);

        List<Product> products = this.list(queryWrapper);
        
        // 缓存结果，有效期15分钟
        redisTemplate.opsForValue().set(cacheKey, products, 15, TimeUnit.MINUTES);
        
        return products;
    }

    @Override
    public void saveProduct(Product product) {
        this.saveOrUpdate(product);
        
        // 清除相关缓存
        clearProductCache(product);
    }

    @Override
    public void batchSaveProducts(List<Product> products) {
        this.saveOrUpdateBatch(products);
        
        // 批量清除缓存
        products.forEach(this::clearProductCache);
    }

    /**
     * 清除商品相关缓存
     */
    private void clearProductCache(Product product) {
        // 清除商品ID缓存
        redisTemplate.delete("product:id:" + product.getId());
        
        // 清除搜索缓存（模糊匹配）
        redisTemplate.keys("product:search:*").forEach(redisTemplate::delete);
        redisTemplate.keys("product:platform:*").forEach(redisTemplate::delete);
        redisTemplate.keys("product:hot:*").forEach(redisTemplate::delete);
    }
}