package com.pricecompare.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pricecompare.entity.CompareTask;
import org.apache.ibatis.annotations.Mapper;

/**
 * 比价任务数据访问接口
 * 
 * @author AutoValuePilot
 */
@Mapper
public interface CompareTaskMapper extends BaseMapper<CompareTask> {
}