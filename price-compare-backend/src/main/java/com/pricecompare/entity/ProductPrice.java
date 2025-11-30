package com.pricecompare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品价格实体类
 * 
 * @author AutoValuePilot
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("product_price")
public class ProductPrice {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 商品ID */
    private Long productId;
    
    /** 平台代码：taobao, jd, pdd, suning, vip */
    private String platformCode;
    
    /** 平台商品ID */
    private String platformProductId;
    
    /** 商品价格 */
    private BigDecimal price;
    
    /** 原价 */
    private BigDecimal originalPrice;
    
    /** 折扣信息 */
    private String discount;
    
    /** 销量 */
    private Integer sales;
    
    /** 评分 */
    private BigDecimal rating;
    
    /** 商品链接 */
    private String productUrl;
    
    /** 店铺名称 */
    private String shopName;
    
    /** 店铺评分 */
    private BigDecimal shopRating;
    
    /** 配送信息 */
    private String delivery;
    
    /** 是否是最低价 */
    private Boolean isLowest;
    
    /** 抓取时间 */
    private LocalDateTime crawlTime;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}