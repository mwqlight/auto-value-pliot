package com.pricecompare.service;

import com.pricecompare.entity.Product;

import java.util.List;

/**
 * 商品服务接口
 * 
 * @author AutoValuePilot
 */
public interface ProductService {

    /**
     * 搜索商品
     * 
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @return 商品列表
     */
    List<Product> searchProducts(String keyword, Integer page, Integer size);

    /**
     * 根据ID获取商品
     * 
     * @param id 商品ID
     * @return 商品信息
     */
    Product getProductById(Long id);

    /**
     * 根据平台获取商品
     * 
     * @param platform 平台名称
     * @param page 页码
     * @param size 每页大小
     * @return 商品列表
     */
    List<Product> getProductsByPlatform(String platform, Integer page, Integer size);

    /**
     * 获取热门商品
     * 
     * @param limit 限制数量
     * @return 热门商品列表
     */
    List<Product> getHotProducts(Integer limit);

    /**
     * 保存商品信息
     * 
     * @param product 商品信息
     */
    void saveProduct(Product product);

    /**
     * 批量保存商品信息
     * 
     * @param products 商品列表
     */
    void batchSaveProducts(List<Product> products);
}