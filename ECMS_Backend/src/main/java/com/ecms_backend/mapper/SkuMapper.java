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

    @Update("<script>" +
            "UPDATE sku SET updated_at = NOW()" +
            "<if test='skuCode != null'>, sku_code = #{skuCode}</if>" +
            "<if test='spuId != null'>, spu_id = #{spuId}</if>" +
            "<if test='specInfo != null'>, spec_info = #{specInfo}</if>" +
            "<if test='marketPrice != null'>, market_price = #{marketPrice}</if>" +
            "<if test='salePrice != null'>, sale_price = #{salePrice}</if>" +
            "<if test='memberPrice != null'>, member_price = #{memberPrice}</if>" +
            "<if test='activityPrice != null'>, activity_price = #{activityPrice}</if>" +
            "<if test='priceStartTime != null'>, price_start_time = #{priceStartTime}</if>" +
            "<if test='priceEndTime != null'>, price_end_time = #{priceEndTime}</if>" +
            "<if test='stock != null'>, stock = #{stock}</if>" +
            "<if test='lockedStock != null'>, locked_stock = #{lockedStock}</if>" +
            "<if test='warnThreshold != null'>, warn_threshold = #{warnThreshold}</if>" +
            "<if test='status != null'>, status = #{status}</if>" +
            " WHERE id = #{id}" +
            "</script>")
    int update(Sku sku);

    @Select("SELECT * FROM sku WHERE id = #{id}")
    Sku selectById(@Param("id") Long id);

    @Select("SELECT * FROM sku WHERE spu_id = #{spuId} ORDER BY id ASC")
    List<Sku> selectBySpuId(@Param("spuId") Long spuId);

    @Update("<script>" +
            "UPDATE sku SET updated_at = NOW()" +
            "<if test='skus != null and skus.size() > 0'>" +
            "<if test='skus[0].stock != null'>" +
            ", stock = CASE id " +
            "<foreach collection='skus' item='sku' separator=' '>" +
            "WHEN #{sku.id} THEN #{sku.stock} " +
            "</foreach>" +
            "END" +
            "</if>" +
            "<if test='skus[0].salePrice != null'>" +
            ", sale_price = CASE id " +
            "<foreach collection='skus' item='sku' separator=' '>" +
            "WHEN #{sku.id} THEN #{sku.salePrice} " +
            "</foreach>" +
            "END" +
            "</if>" +
            "<if test='skus[0].marketPrice != null'>" +
            ", market_price = CASE id " +
            "<foreach collection='skus' item='sku' separator=' '>" +
            "WHEN #{sku.id} THEN #{sku.marketPrice} " +
            "</foreach>" +
            "END" +
            "</if>" +
            "<if test='skus[0].memberPrice != null'>" +
            ", member_price = CASE id " +
            "<foreach collection='skus' item='sku' separator=' '>" +
            "WHEN #{sku.id} THEN #{sku.memberPrice} " +
            "</foreach>" +
            "END" +
            "</if>" +
            "<if test='skus[0].status != null'>" +
            ", status = CASE id " +
            "<foreach collection='skus' item='sku' separator=' '>" +
            "WHEN #{sku.id} THEN #{sku.status} " +
            "</foreach>" +
            "END" +
            "</if>" +
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