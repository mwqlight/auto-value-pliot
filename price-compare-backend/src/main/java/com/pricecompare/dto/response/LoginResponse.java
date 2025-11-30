package com.pricecompare.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录响应DTO
 * 
 * @author AutoValuePilot
 */
@Data
@Schema(description = "登录响应")
public class LoginResponse {
    
    @Schema(description = "用户ID", example = "1")
    private Long userId;
    
    @Schema(description = "用户名", example = "admin")
    private String username;
    
    @Schema(description = "昵称", example = "管理员")
    private String nickname;
    
    @Schema(description = "访问令牌")
    private String accessToken;
    
    @Schema(description = "令牌类型", example = "Bearer")
    private String tokenType;
    
    @Schema(description = "过期时间（毫秒）", example = "86400000")
    private Long expiresIn;
}