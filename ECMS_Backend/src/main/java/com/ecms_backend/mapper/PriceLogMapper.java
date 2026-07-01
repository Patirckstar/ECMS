package com.ecms_backend.mapper;

import com.ecms_backend.entity.PriceLog;
import org.apache.ibatis.annotations.*;

@Mapper
public interface PriceLogMapper {

    @Insert("INSERT INTO price_log (sku_id, price_type, before_price, after_price, operator_id, created_at) " +
            "VALUES (#{skuId}, #{priceType}, #{beforePrice}, #{afterPrice}, #{operatorId}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PriceLog priceLog);
}
