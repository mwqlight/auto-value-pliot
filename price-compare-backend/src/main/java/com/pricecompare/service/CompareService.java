package com.pricecompare.service;

import com.pricecompare.entity.CompareTask;

import java.util.List;

/**
 * 比价服务接口
 * 
 * @author AutoValuePilot
 */
public interface CompareService {
    
    /**
     * 启动比价任务
     */
    CompareTask startCompareTask(String productName);
    
    /**
     * 获取比价任务列表
     */
    List<CompareTask> getCompareTasks(Integer page, Integer pageSize);
    
    /**
     * 根据ID获取比价任务
     */
    CompareTask getCompareTaskById(Long taskId);
    
    /**
     * 获取比价结果
     */
    List<Object> getCompareResults(Long taskId);
    
    /**
     * 删除比价任务
     */
    void deleteCompareTask(Long taskId);
}