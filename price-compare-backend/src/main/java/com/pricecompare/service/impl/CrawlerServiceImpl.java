package com.pricecompare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pricecompare.entity.PlatformConfig;
import com.pricecompare.entity.ProductPrice;
import com.pricecompare.mapper.PlatformConfigMapper;
import com.pricecompare.mapper.ProductPriceMapper;
import com.pricecompare.service.CrawlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 爬虫服务实现类
 * 
 * @author AutoValuePilot
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlerServiceImpl implements CrawlerService {

    private final PlatformConfigMapper platformConfigMapper;
    private final ProductPriceMapper productPriceMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<ProductPrice> searchProducts(String keyword, String platformCode) {
        log.info("开始搜索商品，关键词：{}，平台：{}", keyword, platformCode);
        
        // 检查平台配置
        PlatformConfig platformConfig = getPlatformConfig(platformCode);
        if (platformConfig == null || platformConfig.getEnabled() == 0) {
            log.warn("平台未配置或已禁用：{}", platformCode);
            return new ArrayList<>();
        }

        // 检查缓存
        String cacheKey = "crawler:search:" + platformCode + ":" + keyword;
        List<ProductPrice> cachedResult = (List<ProductPrice>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            log.info("从缓存获取搜索结果，关键词：{}，平台：{}", keyword, platformCode);
            return cachedResult;
        }

        // 模拟爬取数据（实际项目中应调用真实API）
        List<ProductPrice> result = mockCrawlData(keyword, platformCode);
        
        // 缓存结果，有效期10分钟
        redisTemplate.opsForValue().set(cacheKey, result, 10, TimeUnit.MINUTES);
        
        log.info("搜索完成，找到{}个商品，关键词：{}，平台：{}", result.size(), keyword, platformCode);
        return result;
    }

    @Override
    public ProductPrice getProductDetail(String platformProductId, String platformCode) {
        log.info("获取商品详情，商品ID：{}，平台：{}", platformProductId, platformCode);
        
        // 检查平台配置
        PlatformConfig platformConfig = getPlatformConfig(platformCode);
        if (platformConfig == null || platformConfig.getEnabled() == 0) {
            log.warn("平台未配置或已禁用：{}", platformCode);
            return null;
        }

        // 检查缓存
        String cacheKey = "crawler:detail:" + platformCode + ":" + platformProductId;
        ProductPrice cachedResult = (ProductPrice) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            log.info("从缓存获取商品详情，商品ID：{}，平台：{}", platformProductId, platformCode);
            return cachedResult;
        }

        // 模拟爬取数据（实际项目中应调用真实API）
        ProductPrice result = mockCrawlDetail(platformProductId, platformCode);
        
        if (result != null) {
            // 缓存结果，有效期30分钟
            redisTemplate.opsForValue().set(cacheKey, result, 30, TimeUnit.MINUTES);
        }
        
        log.info("商品详情获取完成，商品ID：{}，平台：{}", platformProductId, platformCode);
        return result;
    }

    @Override
    public List<ProductPrice> batchSearchProducts(String keyword, List<String> platformCodes) {
        log.info("批量搜索商品，关键词：{}，平台：{}", keyword, platformCodes);
        
        List<ProductPrice> allResults = new ArrayList<>();
        
        for (String platformCode : platformCodes) {
            try {
                List<ProductPrice> platformResults = searchProducts(keyword, platformCode);
                allResults.addAll(platformResults);
                
                // 添加平台间延迟，避免请求过快
                Thread.sleep(100);
            } catch (Exception e) {
                log.error("平台{}搜索失败：{}", platformCode, e.getMessage());
            }
        }
        
        log.info("批量搜索完成，共找到{}个商品，关键词：{}", allResults.size(), keyword);
        return allResults;
    }

    @Override
    public boolean checkPlatformAvailability(String platformCode) {
        PlatformConfig platformConfig = getPlatformConfig(platformCode);
        return platformConfig != null && platformConfig.getEnabled() == 1;
    }

    @Override
    public List<String> getSupportedPlatforms() {
        QueryWrapper<PlatformConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enabled", 1);
        List<PlatformConfig> configs = platformConfigMapper.selectList(queryWrapper);
        
        return configs.stream()
                .map(PlatformConfig::getPlatformCode)
                .toList();
    }

    /**
     * 获取平台配置
     */
    private PlatformConfig getPlatformConfig(String platformCode) {
        QueryWrapper<PlatformConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("platform_code", platformCode);
        return platformConfigMapper.selectOne(queryWrapper);
    }

    /**
     * 模拟爬取搜索数据
     */
    private List<ProductPrice> mockCrawlData(String keyword, String platformCode) {
        List<ProductPrice> results = new ArrayList<>();
        
        // 模拟不同平台的数据
        switch (platformCode) {
            case "taobao":
                results.addAll(mockTaobaoData(keyword));
                break;
            case "jd":
                results.addAll(mockJdData(keyword));
                break;
            case "pdd":
                results.addAll(mockPddData(keyword));
                break;
            case "suning":
                results.addAll(mockSuningData(keyword));
                break;
            case "vip":
                results.addAll(mockVipData(keyword));
                break;
            default:
                log.warn("不支持的平台：{}", platformCode);
        }
        
        return results;
    }

    /**
     * 模拟爬取商品详情
     */
    private ProductPrice mockCrawlDetail(String platformProductId, String platformCode) {
        // 模拟商品详情数据
        ProductPrice detail = new ProductPrice();
        detail.setPlatformProductId(platformProductId);
        detail.setPlatformCode(platformCode);
        detail.setCrawlTime(LocalDateTime.now());
        detail.setCreateTime(LocalDateTime.now());
        detail.setUpdateTime(LocalDateTime.now());
        detail.setProductUrl("https://" + platformCode + ".com/product/" + platformProductId);
        
        // 根据平台设置不同的价格和详情
        switch (platformCode) {
            case "taobao":
                detail.setPrice(new BigDecimal("299.00"));
                detail.setOriginalPrice(new BigDecimal("399.00"));
                detail.setDiscount("7.5折");
                detail.setSales(1500);
                detail.setRating(new BigDecimal("4.8"));
                detail.setShopName("天猫旗舰店");
                detail.setShopRating(new BigDecimal("4.9"));
                detail.setDelivery("快递 免运费");
                break;
            case "jd":
                detail.setPrice(new BigDecimal("289.00"));
                detail.setOriginalPrice(new BigDecimal("389.00"));
                detail.setDiscount("7.4折");
                detail.setSales(2000);
                detail.setRating(new BigDecimal("4.9"));
                detail.setShopName("京东自营");
                detail.setShopRating(new BigDecimal("4.8"));
                detail.setDelivery("京东物流 次日达");
                break;
            case "pdd":
                detail.setPrice(new BigDecimal("259.00"));
                detail.setOriginalPrice(new BigDecimal("359.00"));
                detail.setDiscount("7.2折");
                detail.setSales(5000);
                detail.setRating(new BigDecimal("4.7"));
                detail.setShopName("拼多多官方店");
                detail.setShopRating(new BigDecimal("4.6"));
                detail.setDelivery("快递 免运费");
                break;
            case "suning":
                detail.setPrice(new BigDecimal("299.00"));
                detail.setSales(1000);
                detail.setRating(new BigDecimal("4.5"));
                detail.setShopName("苏宁易购官方店");
                detail.setDelivery("快递");
                break;
            case "vip":
                detail.setPrice(new BigDecimal("299.00"));
                detail.setSales(1000);
                detail.setRating(new BigDecimal("4.5"));
                detail.setShopName("唯品会官方店");
                detail.setDelivery("快递");
                break;
            default:
                detail.setPrice(new BigDecimal("299.00"));
                detail.setSales(1000);
                detail.setRating(new BigDecimal("4.5"));
                detail.setShopName("官方旗舰店");
                detail.setDelivery("快递");
        }
        
        return detail;
    }

    // 模拟各平台数据的方法
    private List<ProductPrice> mockTaobaoData(String keyword) {
        List<ProductPrice> results = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            ProductPrice item = createMockProduct(keyword, "taobao", i);
            item.setPrice(new BigDecimal(299 + i * 10));
            item.setSales(1000 + i * 200);
            results.add(item);
        }
        return results;
    }

    private List<ProductPrice> mockJdData(String keyword) {
        List<ProductPrice> results = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            ProductPrice item = createMockProduct(keyword, "jd", i);
            item.setPrice(new BigDecimal(289 + i * 15));
            item.setSales(1500 + i * 300);
            results.add(item);
        }
        return results;
    }

    private List<ProductPrice> mockPddData(String keyword) {
        List<ProductPrice> results = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            ProductPrice item = createMockProduct(keyword, "pdd", i);
            item.setPrice(new BigDecimal(259 + i * 8));
            item.setSales(2000 + i * 500);
            results.add(item);
        }
        return results;
    }

    private List<ProductPrice> mockSuningData(String keyword) {
        List<ProductPrice> results = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            ProductPrice item = createMockProduct(keyword, "suning", i);
            item.setPrice(new BigDecimal(279 + i * 12));
            item.setSales(800 + i * 150);
            results.add(item);
        }
        return results;
    }

    private List<ProductPrice> mockVipData(String keyword) {
        List<ProductPrice> results = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            ProductPrice item = createMockProduct(keyword, "vip", i);
            item.setPrice(new BigDecimal(269 + i * 20));
            item.setSales(600 + i * 100);
            results.add(item);
        }
        return results;
    }

    private ProductPrice createMockProduct(String keyword, String platform, int index) {
        ProductPrice item = new ProductPrice();
        item.setPlatformProductId(platform + "_" + keyword.hashCode() + "_" + index);
        item.setPlatformCode(platform);
        item.setCrawlTime(LocalDateTime.now());
        item.setCreateTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        item.setRating(new BigDecimal("4." + (5 + index % 5)));
        item.setShopName(getMockShopName(platform, index));
        item.setShopRating(new BigDecimal("4." + (6 + index % 4)));
        item.setDelivery(getMockDelivery(platform));
        item.setProductUrl("https://" + platform + ".com/product/" + keyword.hashCode() + "_" + index);
        return item;
    }

    private String getMockShopName(String platform, int index) {
        String[] shops = {"官方旗舰店", "品牌专卖店", "优质商家", "金牌卖家", "认证店铺"};
        return platform.toUpperCase() + " " + shops[index % shops.length];
    }

    private String getMockDelivery(String platform) {
        switch (platform) {
            case "jd": return "京东物流 次日达";
            case "taobao": return "快递 免运费";
            case "pdd": return "快递 包邮";
            default: return "快递";
        }
    }
}