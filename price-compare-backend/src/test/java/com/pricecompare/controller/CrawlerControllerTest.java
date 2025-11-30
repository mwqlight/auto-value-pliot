package com.pricecompare.controller;

import com.pricecompare.dto.response.ApiResponse;
import com.pricecompare.service.CrawlerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * 爬虫控制器单元测试
 * 
 * @author AutoValuePilot
 */
@ExtendWith(MockitoExtension.class)
class CrawlerControllerTest {

    @Mock
    private CrawlerService crawlerService;

    @InjectMocks
    private CrawlerController crawlerController;

    @BeforeEach
    void setUp() {
        // 设置模拟数据
        when(crawlerService.getSupportedPlatforms()).thenReturn(Arrays.asList("taobao", "jd", "pdd"));
        when(crawlerService.checkPlatformAvailability("taobao")).thenReturn(true);
        when(crawlerService.checkPlatformAvailability("jd")).thenReturn(true);
        when(crawlerService.checkPlatformAvailability("pdd")).thenReturn(true);
        when(crawlerService.checkPlatformAvailability("unknown")).thenReturn(false);
    }

    @Test
    void testGetSupportedPlatforms() {
        // 测试获取支持的平台列表
        ResponseEntity<ApiResponse<List<String>>> response = crawlerController.getSupportedPlatforms();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        ApiResponse<List<String>> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertEquals(200, apiResponse.getCode());
        assertEquals("成功", apiResponse.getMessage());
        
        List<String> platforms = apiResponse.getData();
        assertNotNull(platforms);
        assertFalse(platforms.isEmpty());
        assertTrue(platforms.contains("taobao"));
        assertTrue(platforms.contains("jd"));
        assertTrue(platforms.contains("pdd"));
    }

    @Test
    void testCheckPlatformAvailability_Available() {
        // 测试检查平台可用性 - 可用
        ResponseEntity<ApiResponse<Boolean>> response = crawlerController.checkPlatformAvailability("taobao");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        ApiResponse<Boolean> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertEquals(200, apiResponse.getCode());
        assertEquals("成功", apiResponse.getMessage());
        assertTrue(apiResponse.getData());
    }

    @Test
    void testCheckPlatformAvailability_Unavailable() {
        // 测试检查平台可用性 - 不可用
        ResponseEntity<ApiResponse<Boolean>> response = crawlerController.checkPlatformAvailability("unknown");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        ApiResponse<Boolean> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertEquals(200, apiResponse.getCode());
        assertEquals("成功", apiResponse.getMessage());
        assertFalse(apiResponse.getData());
    }

    @Test
    void testCheckPlatformAvailability_EmptyPlatformCode() {
        // 测试空平台代码
        ResponseEntity<ApiResponse<Boolean>> response = crawlerController.checkPlatformAvailability("");

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        ApiResponse<Boolean> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertEquals(400, apiResponse.getCode());
        assertEquals("平台代码不能为空", apiResponse.getMessage());
        assertNull(apiResponse.getData());
    }

    @Test
    void testCheckPlatformAvailability_NullPlatformCode() {
        // 测试null平台代码
        ResponseEntity<ApiResponse<Boolean>> response = crawlerController.checkPlatformAvailability(null);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        ApiResponse<Boolean> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertEquals(400, apiResponse.getCode());
        assertEquals("平台代码不能为空", apiResponse.getMessage());
        assertNull(apiResponse.getData());
    }

    @Test
    void testApiResponseStructure() {
        // 测试API响应结构
        ResponseEntity<ApiResponse<List<String>>> response = crawlerController.getSupportedPlatforms();
        
        ApiResponse<List<String>> apiResponse = response.getBody();
        
        // 验证响应结构完整性
        assertNotNull(apiResponse.getCode());
        assertNotNull(apiResponse.getMessage());
        assertNotNull(apiResponse.getTimestamp());
        
        // 验证时间戳格式
        assertTrue(apiResponse.getTimestamp() > 0);
    }

    @Test
    void testControllerExceptionHandling() {
        // 测试控制器异常处理
        when(crawlerService.getSupportedPlatforms()).thenThrow(new RuntimeException("服务异常"));
        
        ResponseEntity<ApiResponse<List<String>>> response = crawlerController.getSupportedPlatforms();
        
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        
        ApiResponse<List<String>> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertEquals(500, apiResponse.getCode());
        assertEquals("系统异常", apiResponse.getMessage());
        assertNull(apiResponse.getData());
    }

    @Test
    void testResponseConsistency() {
        // 测试响应一致性
        ResponseEntity<ApiResponse<List<String>>> response1 = crawlerController.getSupportedPlatforms();
        ResponseEntity<ApiResponse<List<String>>> response2 = crawlerController.getSupportedPlatforms();
        
        // 验证多次调用结果一致
        assertEquals(response1.getStatusCode(), response2.getStatusCode());
        assertEquals(response1.getBody().getCode(), response2.getBody().getCode());
        assertEquals(response1.getBody().getData().size(), response2.getBody().getData().size());
    }
}