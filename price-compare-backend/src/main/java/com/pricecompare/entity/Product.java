package com.pricecompare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 * 
 * @author AutoValuePilot
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("product")
public class Product {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 商品标题 */
    private String title;
    
    /** 商品图片URL */
    private String imageUrl;
    
    /** 商品链接 */
    private String productUrl;
    
    /** 平台类型 */
    private String platform;
    
    /** 平台商品ID */
    private String platformProductId;
    
    /** 价格 */
    private BigDecimal price;
    
    /** 原价 */
    private BigDecimal originalPrice;
    
    /** 销量 */
    private Integer sales;
    
    /** 评分 */
    private BigDecimal rating;
    
    /** 店铺名称 */
    private String shopName;
    
    /** 品牌 */
    private String brand;
    
    /** 型号 */
    private String model;
    
    /** 规格参数（JSON格式） */
    private String specifications;
    
    /** 商品特征（用于相似度匹配） */
    private String features;
    
    /** 商品分类 */
    private String category;
    
    /** 数据来源：0-爬虫，1-API */
    private Integer dataSource;
    
    /** 数据更新时间 */
    private LocalDateTime dataUpdateTime;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}