package com.ecms_backend.mapper;

import com.ecms_backend.entity.SpecTemplate;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SpecTemplateMapper {

    @Select("SELECT * FROM spec_templates WHERE status = 1 ORDER BY sort_order ASC")
    List<SpecTemplate> selectAll();

    @Select("SELECT * FROM spec_templates WHERE id = #{id}")
    SpecTemplate selectById(@Param("id") Long id);

    @Insert("INSERT INTO spec_templates (spec_name, sort_order, status, created_at, updated_at) " +
            "VALUES (#{specName}, #{sortOrder}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SpecTemplate template);

    @Update("UPDATE spec_templates SET spec_name = #{specName}, sort_order = #{sortOrder}, " +
            "status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int update(SpecTemplate template);

    @Delete("DELETE FROM spec_templates WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}