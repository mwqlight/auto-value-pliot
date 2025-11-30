package com.pricecompare.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pricecompare.entity.ProductPrice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品价格Mapper接口
 * 
 * @author AutoValuePilot
 */
@Mapper
public interface ProductPriceMapper extends BaseMapper<ProductPrice> {
}