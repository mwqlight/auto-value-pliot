package com.pricecompare.controller;

import com.pricecompare.dto.response.ApiResponse;
import com.pricecompare.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 数据可视化控制器
 * 
 * @author AutoValuePilot
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "数据可视化", description = "数据统计和可视化接口")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @Operation(summary = "获取统计概览", description = "获取平台数据统计概览")
    public ApiResponse<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = dashboardService.getDashboardStats();
        return ApiResponse.success(stats);
    }

    @GetMapping("/platform-distribution")
    @Operation(summary = "平台分布统计", description = "获取各平台商品分布统计")
    public ApiResponse<Map<String, Long>> getPlatformDistribution() {
        Map<String, Long> distribution = dashboardService.getPlatformDistribution();
        return ApiResponse.success(distribution);
    }

    @GetMapping("/price-trend")
    @Operation(summary = "价格趋势分析", description = "获取商品价格趋势分析")
    public ApiResponse<Map<String, Object>> getPriceTrend() {
        Map<String, Object> trend = dashboardService.getPriceTrend();
        return ApiResponse.success(trend);
    }

    @GetMapping("/task-analysis")
    @Operation(summary = "任务分析", description = "获取比价任务分析数据")
    public ApiResponse<Map<String, Object>> getTaskAnalysis() {
        Map<String, Object> analysis = dashboardService.getTaskAnalysis();
        return ApiResponse.success(analysis);
    }

    @GetMapping("/hot-keywords")
    @Operation(summary = "热门搜索关键词", description = "获取热门搜索关键词统计")
    public ApiResponse<Map<String, Long>> getHotKeywords() {
        Map<String, Long> hotKeywords = dashboardService.getHotKeywords();
        return ApiResponse.success(hotKeywords);
    }
}