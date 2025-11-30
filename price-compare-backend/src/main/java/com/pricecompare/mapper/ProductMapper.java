package com.pricecompare.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pricecompare.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品数据访问接口
 * 
 * @author AutoValuePilot
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}