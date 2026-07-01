package com.ecms_backend.mapper;

import com.ecms_backend.entity.Spu;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface SpuMapper {

        @Insert("INSERT INTO spu (spu_code, spu_name, sub_title, description, brand_id, category_id, " +
                        "product_type, status, is_deleted, seven_day_return, warranty_desc, return_policy, " +
                        "freight_template_id, is_free_shipping, ship_from, ship_hours, audit_status, auditor_id, " +
                        "audit_time, reject_reason, auto_offline, created_by, created_at, updated_at) " +
                        "VALUES (#{spuCode}, #{spuName}, #{subTitle}, #{description}, #{brandId}, #{categoryId}, " +
                        "#{productType}, #{status}, #{isDeleted}, #{sevenDayReturn}, #{warrantyDesc}, #{returnPolicy}, "
                        +
                        "#{freightTemplateId}, #{isFreeShipping}, #{shipFrom}, #{shipHours}, #{auditStatus}, #{auditorId}, "
                        +
                        "#{auditTime}, #{rejectReason}, #{autoOffline}, #{createdBy}, NOW(), NOW())")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(Spu spu);

        @Select("SELECT * FROM spu WHERE id = #{id} AND is_deleted = 0")
        Spu selectById(@Param("id") Long id);

        @Update("UPDATE spu SET spu_name = #{spuName}, sub_title = #{subTitle}, description = #{description}, " +
                        "brand_id = #{brandId}, category_id = #{categoryId}, product_type = #{productType}, " +
                        "seven_day_return = #{sevenDayReturn}, warranty_desc = #{warrantyDesc}, return_policy = #{returnPolicy}, "
                        +
                        "freight_template_id = #{freightTemplateId}, is_free_shipping = #{isFreeShipping}, " +
                        "ship_from = #{shipFrom}, ship_hours = #{shipHours}, auto_offline = #{autoOffline}, " +
                        "updated_at = NOW() WHERE id = #{id}")
        int update(Spu spu);

        @Select("SELECT * FROM spu WHERE spu_code = #{spuCode} AND is_deleted = 0")
        Spu selectByCode(@Param("spuCode") String spuCode);

        @Update("UPDATE spu SET is_deleted = 1, deleted_at = NOW() WHERE id = #{id}")
        int deleteById(@Param("id") Long id);

        @Update("UPDATE spu SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
        int updateStatus(@Param("id") Long id, @Param("status") Integer status);

        @Update("UPDATE spu SET audit_status = #{auditStatus}, auditor_id = #{auditorId}, " +
                        "audit_time = #{auditTime}, reject_reason = #{rejectReason}, updated_at = NOW() WHERE id = #{id}")
        int updateAuditInfo(@Param("id") Long id, @Param("auditStatus") Integer auditStatus,
                        @Param("auditorId") Long auditorId, @Param("auditTime") java.time.LocalDateTime auditTime,
                        @Param("rejectReason") String rejectReason);

        @Select("<script>" +
                        "SELECT spu.*, brands.brand_name, categories.cat_name as categoryName, " +
                        "(SELECT MIN(sku.sale_price) FROM sku WHERE sku.spu_id = spu.id) as minPrice, " +
                        "(SELECT COALESCE(SUM(sku.stock),0) FROM sku WHERE sku.spu_id = spu.id) as totalStock, " +
                        "0 as totalSales " +
                        "FROM spu " +
                        "LEFT JOIN brands ON spu.brand_id = brands.id " +
                        "LEFT JOIN categories ON spu.category_id = categories.id " +
                        "WHERE spu.is_deleted = 0 " +
                        "<if test='params.keyword != null and params.keyword != \"\"'>" +
                        "AND (spu.spu_name LIKE CONCAT('%', #{params.keyword}, '%') OR spu.spu_code LIKE CONCAT('%', #{params.keyword}, '%')) "
                        +
                        "</if>" +
                        "<if test='params.status != null'>" +
                        "AND spu.status = #{params.status} " +
                        "</if>" +
                        "<if test='params.categoryId != null'>" +
                        "AND spu.category_id = #{params.categoryId} " +
                        "</if>" +
                        "<if test='params.tagId != null'>" +
                        "AND EXISTS (SELECT 1 FROM spu_tag WHERE spu_tag.spu_id = spu.id AND spu_tag.tag_id = #{params.tagId}) "
                        +
                        "</if>" +
                        "<if test='params.priceMin != null'>" +
                        "AND EXISTS (SELECT 1 FROM sku WHERE sku.spu_id = spu.id GROUP BY sku.spu_id HAVING MIN(sku.sale_price) &gt;= #{params.priceMin}) " +
                        "</if>" +
                        "<if test='params.priceMax != null'>" +
                        "AND EXISTS (SELECT 1 FROM sku WHERE sku.spu_id = spu.id GROUP BY sku.spu_id HAVING MIN(sku.sale_price) &lt;= #{params.priceMax}) " +
                        "</if>" +
                        "ORDER BY spu.updated_at DESC " +
                        "LIMIT #{params.offset}, #{params.limit}" +
                        "</script>")
        List<Spu> selectList(@Param("params") Map<String, Object> params);

        @Select("<script>" +
                        "SELECT COUNT(*) FROM spu " +
                        "WHERE spu.is_deleted = 0 " +
                        "<if test='params.keyword != null and params.keyword != \"\"'>" +
                        "AND (spu.spu_name LIKE CONCAT('%', #{params.keyword}, '%') OR spu.spu_code LIKE CONCAT('%', #{params.keyword}, '%')) "
                        +
                        "</if>" +
                        "<if test='params.status != null'>" +
                        "AND spu.status = #{params.status} " +
                        "</if>" +
                        "<if test='params.categoryId != null'>" +
                        "AND spu.category_id = #{params.categoryId} " +
                        "</if>" +
                        "<if test='params.tagId != null'>" +
                        "AND EXISTS (SELECT 1 FROM spu_tag WHERE spu_tag.spu_id = spu.id AND spu_tag.tag_id = #{params.tagId}) "
                        +
                        "</if>" +
                        "<if test='params.priceMin != null'>" +
                        "AND EXISTS (SELECT 1 FROM sku WHERE sku.spu_id = spu.id GROUP BY sku.spu_id HAVING MIN(sku.sale_price) &gt;= #{params.priceMin}) " +
                        "</if>" +
                        "<if test='params.priceMax != null'>" +
                        "AND EXISTS (SELECT 1 FROM sku WHERE sku.spu_id = spu.id GROUP BY sku.spu_id HAVING MIN(sku.sale_price) &lt;= #{params.priceMax}) " +
                        "</if>" +
                        "</script>")
        long countList(@Param("params") Map<String, Object> params);
}
