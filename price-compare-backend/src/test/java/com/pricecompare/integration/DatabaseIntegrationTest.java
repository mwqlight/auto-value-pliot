package com.pricecompare.integration;

import com.pricecompare.entity.ProductPrice;
import com.pricecompare.mapper.ProductPriceMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 数据库集成测试
 * 
 * @author AutoValuePilot
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DatabaseIntegrationTest {

    @Autowired
    private ProductPriceMapper productPriceMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void testDatabaseConnection() {
        // 测试数据库连接
        assertNotNull(productPriceMapper);
        
        // 测试基本CRUD操作
        ProductPrice product = new ProductPrice();
        product.setTitle("测试商品");
        product.setPlatformCode("taobao");
        product.setPlatformProductId("test_001");
        product.setPrice(new BigDecimal("100.00"));
        product.setOriginalPrice(new BigDecimal("120.00"));
        product.setRating(new BigDecimal("4.5"));
        product.setShopName("测试店铺");
        product.setDelivery("顺丰快递");
        product.setCrawlTime(LocalDateTime.now());

        // 保存
        int saveResult = productPriceMapper.insert(product);
        assertEquals(1, saveResult);
        assertNotNull(product.getId());

        // 查询
        List<ProductPrice> products = productPriceMapper.selectList(null);
        assertFalse(products.isEmpty());

        // 更新
        product.setPrice(new BigDecimal("90.00"));
        int updateResult = productPriceMapper.updateById(product);
        assertEquals(1, updateResult);
        ProductPrice updatedProduct = productPriceMapper.selectById(product.getId());
        assertEquals(0, new BigDecimal("90.00").compareTo(updatedProduct.getPrice()));

        // 删除
        int deleteResult = productPriceMapper.deleteById(updatedProduct.getId());
        assertEquals(1, deleteResult);
        ProductPrice deletedProduct = productPriceMapper.selectById(updatedProduct.getId());
        assertNull(deletedProduct);
    }

    @Test
    void testRedisConnection() {
        // 测试Redis连接
        assertNotNull(redisTemplate);

        // 测试基本操作
        String key = "test:key";
        String value = "test_value";

        // 设置值
        redisTemplate.opsForValue().set(key, value, 60, TimeUnit.SECONDS);

        // 获取值
        Object retrievedValue = redisTemplate.opsForValue().get(key);
        assertEquals(value, retrievedValue);

        // 测试过期时间
        redisTemplate.delete(key);
        assertNull(redisTemplate.opsForValue().get(key));
    }

    @Test
    void testProductPriceRepositoryQueries() {
        // 测试自定义查询方法
        ProductPrice product1 = new ProductPrice();
        product1.setTitle("iPhone 15");
        product1.setPlatformCode("taobao");
        product1.setPlatformProductId("taobao_iphone15_001");
        product1.setPrice(new BigDecimal("5999"));
        product1.setCrawlTime(LocalDateTime.now());

        ProductPrice product2 = new ProductPrice();
        product2.setTitle("iPhone 15");
        product2.setPlatformCode("jd");
        product2.setPlatformProductId("jd_iphone15_001");
        product2.setPrice(new BigDecimal("5899"));
        product2.setCrawlTime(LocalDateTime.now());

        productPriceRepository.saveAll(List.of(product1, product2));

        // 测试按平台查询
        List<ProductPrice> taobaoProducts = productPriceRepository.findByPlatformCode("taobao");
        assertEquals(1, taobaoProducts.size());
        assertEquals("taobao", taobaoProducts.get(0).getPlatformCode());

        // 测试按标题模糊查询
        List<ProductPrice> iphoneProducts = productPriceRepository.findByTitleContaining("iPhone");
        assertEquals(2, iphoneProducts.size());

        // 测试按价格范围查询
        List<ProductPrice> priceRangeProducts = productPriceRepository.findByPriceBetween(
            new BigDecimal("5800"), new BigDecimal("6000"));
        assertEquals(2, priceRangeProducts.size());
    }

    @Test
    void testRedisCacheIntegration() {
        // 测试Redis缓存集成
        String cacheKey = "product:search:iPhone15";
        List<ProductPrice> mockProducts = List.of(
            createMockProduct("iPhone 15", "taobao", "taobao_001", new BigDecimal("5999")),
            createMockProduct("iPhone 15", "jd", "jd_001", new BigDecimal("5899"))
        );

        // 设置缓存
        redisTemplate.opsForValue().set(cacheKey, mockProducts, 300, TimeUnit.SECONDS);

        // 获取缓存
        @SuppressWarnings("unchecked")
        List<ProductPrice> cachedProducts = (List<ProductPrice>) redisTemplate.opsForValue().get(cacheKey);
        
        assertNotNull(cachedProducts);
        assertEquals(2, cachedProducts.size());

        // 验证缓存数据
        ProductPrice firstProduct = cachedProducts.get(0);
        assertEquals("iPhone 15", firstProduct.getTitle());
        assertEquals("taobao", firstProduct.getPlatformCode());
        assertEquals(0, new BigDecimal("5999").compareTo(firstProduct.getPrice()));

        // 清理缓存
        redisTemplate.delete(cacheKey);
        assertNull(redisTemplate.opsForValue().get(cacheKey));
    }

    @Test
    void testTransactionRollback() {
        // 测试事务回滚
        ProductPrice product = new ProductPrice();
        product.setTitle("事务测试商品");
        product.setPlatformCode("taobao");
        product.setPlatformProductId("transaction_test_001");
        product.setPrice(new BigDecimal("100.00"));
        product.setCrawlTime(LocalDateTime.now());

        try {
            // 模拟事务操作
            ProductPrice savedProduct = productPriceRepository.save(product);
            assertNotNull(savedProduct.getId());

            // 故意抛出异常触发回滚
            throw new RuntimeException("事务回滚测试");
        } catch (Exception e) {
            // 验证事务已回滚，数据不存在
            List<ProductPrice> products = productPriceRepository.findByTitleContaining("事务测试");
            assertTrue(products.isEmpty());
        }
    }

    private ProductPrice createMockProduct(String title, String platformCode, 
                                          String platformProductId, BigDecimal price) {
        ProductPrice product = new ProductPrice();
        product.setTitle(title);
        product.setPlatformCode(platformCode);
        product.setPlatformProductId(platformProductId);
        product.setPrice(price);
        product.setCrawlTime(LocalDateTime.now());
        return product;
    }
}