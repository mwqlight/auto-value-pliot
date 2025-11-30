package com.pricecompare.service;

import com.pricecompare.entity.ProductPrice;

import java.util.List;

/**
 * 爬虫服务接口
 * 
 * @author AutoValuePilot
 */
public interface CrawlerService {
    
    /**
     * 搜索商品
     * @param keyword 搜索关键词
     * @param platformCode 平台代码
     * @return 商品价格列表
     */
    List<ProductPrice> searchProducts(String keyword, String platformCode);
    
    /**
     * 获取商品详情
     * @param platformProductId 平台商品ID
     * @param platformCode 平台代码
     * @return 商品价格信息
     */
    ProductPrice getProductDetail(String platformProductId, String platformCode);
    
    /**
     * 批量搜索商品（多平台）
     * @param keyword 搜索关键词
     * @param platformCodes 平台代码列表
     * @return 商品价格列表
     */
    List<ProductPrice> batchSearchProducts(String keyword, List<String> platformCodes);
    
    /**
     * 检查平台是否可用
     * @param platformCode 平台代码
     * @return 是否可用
     */
    boolean checkPlatformAvailability(String platformCode);
    
    /**
     * 获取支持的平台列表
     * @return 平台代码列表
     */
    List<String> getSupportedPlatforms();
}