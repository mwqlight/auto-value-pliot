package com.pricecompare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 平台授权信息实体类
 * 
 * @author AutoValuePilot
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("platform_auth")
public class PlatformAuth {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 平台类型：taobao, jd, pdd, suning, vip */
    private String platformType;
    
    /** 平台用户ID */
    private String platformUserId;
    
    /** 访问令牌 */
    private String accessToken;
    
    /** 刷新令牌 */
    private String refreshToken;
    
    /** 令牌过期时间 */
    private LocalDateTime tokenExpireTime;
    
    /** 授权状态：0-未授权，1-已授权，2-已过期 */
    private Integer authStatus;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}