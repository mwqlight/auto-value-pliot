package com.pricecompare.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 比价请求DTO
 * 
 * @author AutoValuePilot
 */
@Data
@Schema(description = "比价请求")
public class CompareRequest {
    
    @NotNull(message = "商品ID不能为空")
    @Schema(description = "商品ID", example = "1")
    private Long productId;
    
    @NotBlank(message = "源平台不能为空")
    @Schema(description = "源平台", example = "taobao")
    private String sourcePlatform;
    
    @NotBlank(message = "商品名称不能为空")
    @Schema(description = "商品名称", example = "iPhone 15")
    private String productName;
    
    @Schema(description = "商品特征（JSON格式）")
    private String productFeatures;
}