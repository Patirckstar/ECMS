package com.ecms_backend.mapper;

import com.ecms_backend.entity.SpecTemplate;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SpecTemplateMapper {

    @Select("SELECT * FROM spec_templates WHERE status = 1 ORDER BY sort_order ASC")
    List<SpecTemplate> selectAll();
}
