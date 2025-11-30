package com.pricecompare.controller;

import com.pricecompare.dto.request.CompareRequest;
import com.pricecompare.dto.response.ApiResponse;
import com.pricecompare.entity.CompareTask;
import com.pricecompare.service.CompareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 比价任务控制器
 * 
 * @author AutoValuePilot
 */
@RestController
@RequestMapping("/api/v1/compare")
@RequiredArgsConstructor
@Tag(name = "比价管理", description = "比价任务管理接口")
public class CompareController {

    private final CompareService compareService;

    @PostMapping("/start")
    @Operation(summary = "启动比价任务", description = "根据商品名称启动比价任务")
    public ApiResponse<CompareTask> startCompareTask(@RequestBody CompareRequest request) {
        CompareTask task = compareService.startCompareTask(request.getProductName());
        return ApiResponse.success(task);
    }

    @GetMapping("/tasks")
    @Operation(summary = "获取比价任务列表", description = "获取用户的历史比价任务")
    public ApiResponse<List<CompareTask>> getCompareTasks(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        List<CompareTask> tasks = compareService.getCompareTasks(page, size);
        return ApiResponse.success(tasks);
    }

    @GetMapping("/tasks/{taskId}")
    @Operation(summary = "获取比价任务详情", description = "根据任务ID获取比价任务详情")
    public ApiResponse<CompareTask> getCompareTaskById(@PathVariable Long taskId) {
        CompareTask task = compareService.getCompareTaskById(taskId);
        return ApiResponse.success(task);
    }

    @GetMapping("/tasks/{taskId}/products")
    @Operation(summary = "获取比价结果", description = "获取比价任务中的商品列表")
    public ApiResponse<List<Object>> getCompareResults(@PathVariable Long taskId) {
        List<Object> results = compareService.getCompareResults(taskId);
        return ApiResponse.success(results);
    }

    @DeleteMapping("/tasks/{taskId}")
    @Operation(summary = "删除比价任务", description = "删除指定的比价任务")
    public ApiResponse<Void> deleteCompareTask(@PathVariable Long taskId) {
        compareService.deleteCompareTask(taskId);
        return ApiResponse.success();
    }
}