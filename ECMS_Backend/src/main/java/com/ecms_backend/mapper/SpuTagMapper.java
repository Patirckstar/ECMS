package com.ecms_backend.mapper;

import com.ecms_backend.entity.SpuTag;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SpuTagMapper {

    @Insert("INSERT INTO spu_tag (spu_id, tag_id, created_at) VALUES (#{spuId}, #{tagId}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SpuTag spuTag);

    @Delete("DELETE FROM spu_tag WHERE spu_id = #{spuId}")
    int deleteBySpuId(@Param("spuId") Long spuId);

    @Select("SELECT * FROM spu_tag WHERE spu_id = #{spuId}")
    List<SpuTag> selectBySpuId(@Param("spuId") Long spuId);
}
