package com.ecms_backend.mapper;

import com.ecms_backend.entity.SpuImage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SpuImageMapper {

    @Insert("INSERT INTO spu_images (spu_id, image_type, image_url, video_url, sort_order, is_cover, created_at) " +
            "VALUES (#{spuId}, #{imageType}, #{imageUrl}, #{videoUrl}, #{sortOrder}, #{isCover}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SpuImage spuImage);

    @Delete("DELETE FROM spu_images WHERE spu_id = #{spuId}")
    int deleteBySpuId(@Param("spuId") Long spuId);

    @Select("SELECT * FROM spu_images WHERE spu_id = #{spuId} ORDER BY image_type ASC, sort_order ASC")
    List<SpuImage> selectBySpuId(@Param("spuId") Long spuId);

    @Select("<script>SELECT * FROM spu_images WHERE spu_id IN " +
            "<foreach collection='spuIds' item='spuId' open='(' separator=',' close=')'>#{spuId}</foreach> " +
            "ORDER BY image_type ASC, sort_order ASC</script>")
    List<SpuImage> selectBySpuIds(@Param("spuIds") List<Long> spuIds);
}
