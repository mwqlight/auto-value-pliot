package com.pricecompare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 比价任务实体类
 * 
 * @author AutoValuePilot
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("compare_task")
public class CompareTask {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 任务ID（UUID） */
    private String taskId;
    
    /** 用户ID */
    private Long userId;
    
    /** 源商品ID */
    private Long sourceProductId;
    
    /** 源平台 */
    private String sourcePlatform;
    
    /** 商品名称 */
    private String productName;
    
    /** 任务状态：0-待处理，1-处理中，2-已完成，3-失败 */
    private Integer status;
    
    /** 任务进度（0-100） */
    private Integer progress;
    
    /** 任务结束时间 */
    private LocalDateTime endTime;
    
    /** 错误信息 */
    private String errorMessage;
    
    /** 比价结果（JSON格式） */
    private String compareResult;
    
    /** 相似商品推荐（JSON格式） */
    private String similarProducts;
    
    /** 图表数据（JSON格式） */
    private String chartData;
    
    /** 任务开始时间 */
    private LocalDateTime startTime;
    
    /** 任务完成时间 */
    private LocalDateTime finishTime;
    
    /** 比价结果数量 */
    private Integer resultCount;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}