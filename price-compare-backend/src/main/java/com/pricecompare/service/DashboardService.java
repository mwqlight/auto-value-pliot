package com.pricecompare.service;

import java.util.Map;

/**
 * 数据可视化服务接口
 * 
 * @author AutoValuePilot
 */
public interface DashboardService {

    /**
     * 获取统计概览
     * 
     * @return 统计概览数据
     */
    Map<String, Object> getDashboardStats();

    /**
     * 获取平台分布统计
     * 
     * @return 平台分布数据
     */
    Map<String, Long> getPlatformDistribution();

    /**
     * 获取价格趋势分析
     * 
     * @return 价格趋势数据
     */
    Map<String, Object> getPriceTrend();

    /**
     * 获取比价任务分析数据
     * 
     * @return 任务分析数据
     */
    Map<String, Object> getTaskAnalysis();

    /**
     * 获取热门搜索关键词
     * 
     * @return 热门关键词数据
     */
    Map<String, Long> getHotKeywords();
}