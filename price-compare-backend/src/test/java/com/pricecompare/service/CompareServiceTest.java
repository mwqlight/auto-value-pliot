package com.pricecompare.service;

import com.pricecompare.entity.ProductPrice;
import com.pricecompare.service.impl.CompareServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * 比价服务单元测试
 * 
 * @author AutoValuePilot
 */
@ExtendWith(MockitoExtension.class)
class CompareServiceTest {

    @Mock
    private CrawlerService crawlerService;

    @InjectMocks
    private CompareServiceImpl compareService;

    private List<ProductPrice> mockProductData;

    @BeforeEach
    void setUp() {
        // 准备模拟数据
        mockProductData = Arrays.asList(
            createMockProduct("iPhone 15", "taobao", "taobao_iphone15_001", 
                new BigDecimal("5999"), new BigDecimal("6499"), new BigDecimal("4.8"), 
                "Apple旗舰店", "顺丰快递", LocalDateTime.now().minusHours(1)),
            
            createMockProduct("iPhone 15", "jd", "jd_iphone15_001", 
                new BigDecimal("5899"), new BigDecimal("6299"), new BigDecimal("4.9"), 
                "京东自营", "京东物流", LocalDateTime.now().minusHours(2)),
            
            createMockProduct("iPhone 15", "pdd", "pdd_iphone15_001", 
                new BigDecimal("5799"), new BigDecimal("5999"), new BigDecimal("4.7"), 
                "百亿补贴", "极兔快递", LocalDateTime.now().minusHours(3)),
            
            createMockProduct("iPhone 15 Pro", "taobao", "taobao_iphone15pro_001", 
                new BigDecimal("7999"), new BigDecimal("8499"), new BigDecimal("4.9"), 
                "Apple旗舰店", "顺丰快递", LocalDateTime.now().minusHours(1))
        );
    }

    private ProductPrice createMockProduct(String title, String platformCode, String platformProductId,
                                          BigDecimal price, BigDecimal originalPrice, BigDecimal rating,
                                          String shopName, String delivery, LocalDateTime crawlTime) {
        ProductPrice product = new ProductPrice();
        // 注意：ProductPrice实体没有setTitle()方法，使用其他字段
        product.setPlatformCode(platformCode);
        product.setPlatformProductId(platformProductId);
        product.setPrice(price);
        product.setOriginalPrice(originalPrice);
        product.setRating(rating);
        product.setShopName(shopName);
        product.setDelivery(delivery);
        product.setCrawlTime(crawlTime);
        return product;
    }

    @Test
    void testGetCompareResults_ExactMatch() {
        // 测试精确匹配比价
        String keyword = "iPhone 15";
        
        when(crawlerService.searchProducts(keyword, null)).thenReturn(mockProductData);
        
        Map<String, Object> result = compareService.getCompareResults(keyword);
        
        assertNotNull(result);
        assertTrue(result.containsKey("products"));
        assertTrue(result.containsKey("summary"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> products = (List<Map<String, Object>>) result.get("products");
        
        // 验证返回的商品数量
        assertEquals(3, products.size());
        
        // 验证价格排序（从低到高）
        BigDecimal firstPrice = new BigDecimal(products.get(0).get("price").toString());
        BigDecimal secondPrice = new BigDecimal(products.get(1).get("price").toString());
        BigDecimal thirdPrice = new BigDecimal(products.get(2).get("price").toString());
        
        assertTrue(firstPrice.compareTo(secondPrice) <= 0);
        assertTrue(secondPrice.compareTo(thirdPrice) <= 0);
    }

    @Test
    void testGetCompareResults_EmptyResult() {
        // 测试无结果比价
        String keyword = "不存在的商品";
        
        when(crawlerService.searchProducts(keyword, null)).thenReturn(Arrays.asList());
        
        Map<String, Object> result = compareService.getCompareResults(keyword);
        
        assertNotNull(result);
        assertTrue(result.containsKey("products"));
        assertTrue(result.containsKey("summary"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> products = (List<Map<String, Object>>) result.get("products");
        
        assertTrue(products.isEmpty());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> summary = (Map<String, Object>) result.get("summary");
        
        assertEquals(0, summary.get("totalCount"));
        assertEquals("无匹配商品", summary.get("message"));
    }

    @Test
    void testGetCompareResults_SinglePlatform() {
        // 测试单平台比价
        String keyword = "iPhone 15";
        String platformCode = "taobao";
        
        List<ProductPrice> taobaoProducts = mockProductData.stream()
                .filter(p -> p.getPlatformCode().equals(platformCode))
                .toList();
        
        when(crawlerService.searchProducts(keyword, platformCode)).thenReturn(taobaoProducts);
        
        Map<String, Object> result = compareService.getCompareResults(keyword, platformCode);
        
        assertNotNull(result);
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> products = (List<Map<String, Object>>) result.get("products");
        
        assertEquals(1, products.size());
        assertEquals("taobao", products.get(0).get("platformCode"));
    }

    @Test
    void testGetCompareResults_PriceAnalysis() {
        // 测试价格分析功能
        String keyword = "iPhone 15";
        
        when(crawlerService.searchProducts(keyword, null)).thenReturn(mockProductData);
        
        Map<String, Object> result = compareService.getCompareResults(keyword);
        
        assertNotNull(result);
        assertTrue(result.containsKey("summary"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> summary = (Map<String, Object>) result.get("summary");
        
        // 验证价格统计信息
        assertNotNull(summary.get("minPrice"));
        assertNotNull(summary.get("maxPrice"));
        assertNotNull(summary.get("avgPrice"));
        assertNotNull(summary.get("totalCount"));
        
        BigDecimal minPrice = new BigDecimal(summary.get("minPrice").toString());
        BigDecimal maxPrice = new BigDecimal(summary.get("maxPrice").toString());
        
        assertTrue(minPrice.compareTo(maxPrice) <= 0);
        assertEquals(3, summary.get("totalCount"));
    }

    @Test
    void testGetCompareResults_PlatformDistribution() {
        // 测试平台分布统计
        String keyword = "iPhone 15";
        
        when(crawlerService.searchProducts(keyword, null)).thenReturn(mockProductData);
        
        Map<String, Object> result = compareService.getCompareResults(keyword);
        
        assertNotNull(result);
        assertTrue(result.containsKey("summary"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> summary = (Map<String, Object>) result.get("summary");
        
        // 验证平台分布
        assertTrue(summary.containsKey("platformDistribution"));
        
        @SuppressWarnings("unchecked")
        Map<String, Integer> platformDistribution = (Map<String, Integer>) summary.get("platformDistribution");
        
        assertEquals(1, platformDistribution.get("taobao"));
        assertEquals(1, platformDistribution.get("jd"));
        assertEquals(1, platformDistribution.get("pdd"));
    }

    @Test
    void testGetCompareResults_DataQuality() {
        // 测试数据质量验证
        String keyword = "iPhone 15";
        
        when(crawlerService.searchProducts(keyword, null)).thenReturn(mockProductData);
        
        Map<String, Object> result = compareService.getCompareResults(keyword);
        
        assertNotNull(result);
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> products = (List<Map<String, Object>>) result.get("products");
        
        // 验证每个商品的数据完整性
        for (Map<String, Object> product : products) {
            assertNotNull(product.get("title"));
            assertNotNull(product.get("price"));
            assertNotNull(product.get("platformCode"));
            assertNotNull(product.get("platformProductId"));
            
            // 验证价格为正数
            BigDecimal price = new BigDecimal(product.get("price").toString());
            assertTrue(price.compareTo(BigDecimal.ZERO) > 0);
        }
    }

    @Test
    void testGetCompareResults_ErrorHandling() {
        // 测试异常处理
        String keyword = "iPhone 15";
        
        when(crawlerService.searchProducts(keyword, null)).thenThrow(new RuntimeException("网络错误"));
        
        Map<String, Object> result = compareService.getCompareResults(keyword);
        
        assertNotNull(result);
        assertTrue(result.containsKey("products"));
        assertTrue(result.containsKey("summary"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> products = (List<Map<String, Object>>) result.get("products");
        
        assertTrue(products.isEmpty());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> summary = (Map<String, Object>) result.get("summary");
        
        assertEquals("搜索失败，请稍后重试", summary.get("message"));
    }
}