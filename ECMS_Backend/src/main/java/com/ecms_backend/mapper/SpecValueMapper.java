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
}
