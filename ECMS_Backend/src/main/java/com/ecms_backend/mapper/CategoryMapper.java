package com.ecms_backend.mapper;

import com.ecms_backend.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Select("SELECT * FROM categories WHERE status = 1 ORDER BY sort_order ASC")
    List<Category> selectAll();

    @Select("SELECT * FROM categories WHERE parent_id = #{parentId} AND status = 1 ORDER BY sort_order ASC")
    List<Category> selectByParentId(@Param("parentId") Long parentId);

    @Select("SELECT * FROM categories WHERE level = #{level} AND status = 1 ORDER BY sort_order ASC")
    List<Category> selectByLevel(@Param("level") Integer level);
}
