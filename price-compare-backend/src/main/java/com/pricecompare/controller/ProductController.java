package com.pricecompare.controller;

import com.pricecompare.dto.response.ApiResponse;
import com.pricecompare.entity.Product;
import com.pricecompare.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品管理控制器
 * 
 * @author AutoValuePilot
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "商品管理", description = "商品信息管理接口")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/search")
    @Operation(summary = "搜索商品", description = "根据关键词搜索商品")
    public ApiResponse<List<Product>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        List<Product> products = productService.searchProducts(keyword, page, size);
        return ApiResponse.success(products);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取商品详情", description = "根据ID获取商品详细信息")
    public ApiResponse<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ApiResponse.success(product);
    }

    @GetMapping("/platform/{platform}")
    @Operation(summary = "获取平台商品", description = "根据平台获取商品列表")
    public ApiResponse<List<Product>> getProductsByPlatform(
            @PathVariable String platform,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        List<Product> products = productService.getProductsByPlatform(platform, page, size);
        return ApiResponse.success(products);
    }

    @GetMapping("/hot")
    @Operation(summary = "获取热门商品", description = "获取热门搜索商品列表")
    public ApiResponse<List<Product>> getHotProducts(@RequestParam(defaultValue = "10") Integer limit) {
        List<Product> products = productService.getHotProducts(limit);
        return ApiResponse.success(products);
    }
}