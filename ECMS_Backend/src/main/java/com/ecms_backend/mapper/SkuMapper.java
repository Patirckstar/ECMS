package com.ecms_backend.mapper;

import com.ecms_backend.entity.Sku;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SkuMapper {

    @Insert("INSERT INTO sku (sku_code, spu_id, spec_info, market_price, sale_price, member_price, " +
            "activity_price, price_start_time, price_end_time, stock, locked_stock, warn_threshold, status, created_at, updated_at) " +
            "VALUES (#{skuCode}, #{spuId}, #{specInfo}, #{marketPrice}, #{salePrice}, #{memberPrice}, " +
            "#{activityPrice}, #{priceStartTime}, #{priceEndTime}, #{stock}, #{lockedStock}, #{warnThreshold}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Sku sku);

    @Update("UPDATE sku SET sku_code = #{skuCode}, spu_id = #{spuId}, spec_info = #{specInfo}, " +
            "market_price = #{marketPrice}, sale_price = #{salePrice}, member_price = #{memberPrice}, " +
            "activity_price = #{activityPrice}, price_start_time = #{priceStartTime}, price_end_time = #{priceEndTime}, " +
            "stock = #{stock}, locked_stock = #{lockedStock}, warn_threshold = #{warnThreshold}, " +
            "status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int update(Sku sku);

    @Select("SELECT * FROM sku WHERE id = #{id}")
    Sku selectById(@Param("id") Long id);

    @Select("SELECT * FROM sku WHERE spu_id = #{spuId} ORDER BY id ASC")
    List<Sku> selectBySpuId(@Param("spuId") Long spuId);

    @Update("<script>" +
            "UPDATE sku SET updated_at = NOW()" +
            "<if test='skus != null and skus.size() > 0'>" +
            ", stock = CASE id " +
            "<foreach collection='skus' item='sku' separator=' '>" +
            "WHEN #{sku.id} THEN #{sku.stock} " +
            "</foreach>" +
            "END" +
            ", sale_price = CASE id " +
            "<foreach collection='skus' item='sku' separator=' '>" +
            "WHEN #{sku.id} THEN #{sku.salePrice} " +
            "</foreach>" +
            "END" +
            ", status = CASE id " +
            "<foreach collection='skus' item='sku' separator=' '>" +
            "WHEN #{sku.id} THEN #{sku.status} " +
            "</foreach>" +
            "END" +
            "</if>" +
            " WHERE id IN " +
            "<foreach collection='skus' item='sku' open='(' separator=',' close=')'>" +
            "#{sku.id}" +
            "</foreach>" +
            "</script>")
    int batchUpdate(@Param("skus") List<Sku> skus);

    @Delete("DELETE FROM sku WHERE spu_id = #{spuId}")
    int deleteBySpuId(@Param("spuId") Long spuId);
}
