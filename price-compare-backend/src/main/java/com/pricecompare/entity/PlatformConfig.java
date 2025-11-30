package com.pricecompare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 平台配置实体类
 * 
 * @author AutoValuePilot
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("platform_config")
public class PlatformConfig {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 平台名称 */
    private String platformName;
    
    /** 平台代码：taobao, jd, pdd, suning, vip */
    private String platformCode;
    
    /** 平台API基础URL */
    private String apiBaseUrl;
    
    /** 搜索API路径 */
    private String searchApiPath;
    
    /** 商品详情API路径 */
    private String detailApiPath;
    
    /** 请求超时时间（毫秒） */
    private Integer timeout;
    
    /** 最大重试次数 */
    private Integer maxRetries;
    
    /** 是否启用：0-禁用，1-启用 */
    private Integer enabled;
    
    /** 请求频率限制（毫秒） */
    private Integer rateLimit;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}