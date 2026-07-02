package com.ecms_backend.mapper;

import com.ecms_backend.entity.SpecValue;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SpecValueMapper {

    @Select("SELECT * FROM spec_values WHERE spec_id = #{specId} ORDER BY sort_order ASC")
    List<SpecValue> selectBySpecId(@Param("specId") Long specId);

    @Select("SELECT * FROM spec_values ORDER BY sort_order ASC")
    List<SpecValue> selectAll();

    @Select("SELECT * FROM spec_values WHERE id = #{id}")
    SpecValue selectById(@Param("id") Long id);

    @Insert("INSERT INTO spec_values (spec_id, value_name, sort_order, created_at) " +
            "VALUES (#{specId}, #{valueName}, #{sortOrder}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SpecValue value);

    @Update("UPDATE spec_values SET value_name = #{valueName}, sort_order = #{sortOrder} WHERE id = #{id}")
    int update(SpecValue value);

    @Delete("DELETE FROM spec_values WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Delete("DELETE FROM spec_values WHERE spec_id = #{specId}")
    int deleteBySpecId(@Param("specId") Long specId);
}