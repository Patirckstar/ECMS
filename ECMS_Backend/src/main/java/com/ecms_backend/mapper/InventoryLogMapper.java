package com.ecms_backend.mapper;

import com.ecms_backend.entity.InventoryLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface InventoryLogMapper {

    @Insert("INSERT INTO inventory_log (sku_id, spu_id, change_type, change_qty, before_stock, after_stock, " +
            "operator_id, remark, created_at) " +
            "VALUES (#{skuId}, #{spuId}, #{changeType}, #{changeQty}, #{beforeStock}, #{afterStock}, " +
            "#{operatorId}, #{remark}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(InventoryLog inventoryLog);

    @Select("<script>" +
            "SELECT * FROM inventory_log WHERE spu_id = #{spuId}" +
            "<if test='changeType != null'> AND change_type = #{changeType}</if>" +
            " ORDER BY created_at DESC" +
            " LIMIT #{offset}, #{limit}" +
            "</script>")
    List<InventoryLog> selectBySpuId(@Param("spuId") Long spuId,
                                     @Param("changeType") Integer changeType,
                                     @Param("offset") Integer offset,
                                     @Param("limit") Integer limit);

    @Select("<script>" +
            "SELECT COUNT(*) FROM inventory_log WHERE spu_id = #{spuId}" +
            "<if test='changeType != null'> AND change_type = #{changeType}</if>" +
            "</script>")
    long countBySpuId(@Param("spuId") Long spuId, @Param("changeType") Integer changeType);
}
