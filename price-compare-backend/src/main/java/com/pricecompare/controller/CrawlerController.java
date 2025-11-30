package com.pricecompare.controller;

import com.pricecompare.dto.response.ApiResponse;
import com.pricecompare.entity.ProductPrice;
import com.pricecompare.service.CrawlerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 爬虫控制器
 * 
 * @author AutoValuePilot
 */
@Tag(name = "爬虫管理", description = "电商平台数据爬取接口")
@RestController
@RequestMapping("/api/crawler")
@RequiredArgsConstructor
public class CrawlerController {

    private final CrawlerService crawlerService;

    @GetMapping("/search")
    @Operation(summary = "搜索商品", description = "在指定平台搜索商品")
    public ApiResponse<List<ProductPrice>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(required = false) String platformCode) {
        
        try {
            List<ProductPrice> results;
            if (platformCode != null && !platformCode.isEmpty()) {
                // 单平台搜索
                results = crawlerService.searchProducts(keyword, platformCode);
            } else {
                // 多平台搜索
                List<String> supportedPlatforms = crawlerService.getSupportedPlatforms();
                results = crawlerService.batchSearchProducts(keyword, supportedPlatforms);
            }
            
            return ApiResponse.success(results);
        } catch (Exception e) {
            return ApiResponse.error(500, "搜索失败：" + e.getMessage());
        }
    }

    @GetMapping("/detail")
    @Operation(summary = "获取商品详情", description = "获取指定商品的详细信息")
    public ApiResponse<ProductPrice> getProductDetail(
            @RequestParam String platformProductId,
            @RequestParam String platformCode) {
        
        try {
            ProductPrice detail = crawlerService.getProductDetail(platformProductId, platformCode);
            if (detail == null) {
                return ApiResponse.error(404, "商品不存在");
            }
            return ApiResponse.success(detail);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取详情失败：" + e.getMessage());
        }
    }

    @GetMapping("/platforms")
    @Operation(summary = "获取支持的平台", description = "获取当前可用的电商平台列表")
    public ApiResponse<List<String>> getSupportedPlatforms() {
        try {
            List<String> platforms = crawlerService.getSupportedPlatforms();
            return ApiResponse.success(platforms);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取平台列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/platform/status")
    @Operation(summary = "检查平台状态", description = "检查指定平台是否可用")
    public ApiResponse<Boolean> checkPlatformStatus(@RequestParam String platformCode) {
        try {
            boolean available = crawlerService.checkPlatformAvailability(platformCode);
            return ApiResponse.success(available);
        } catch (Exception e) {
            return ApiResponse.error(500, "检查平台状态失败：" + e.getMessage());
        }
    }
}