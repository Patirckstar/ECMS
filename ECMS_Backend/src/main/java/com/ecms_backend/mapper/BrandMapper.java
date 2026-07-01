package com.ecms_backend.mapper;

import com.ecms_backend.entity.Brand;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BrandMapper {

    @Select("SELECT * FROM brands WHERE status = 1 ORDER BY sort_order ASC")
    List<Brand> selectAll();
}
