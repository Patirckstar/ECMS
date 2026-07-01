package com.ecms_backend.mapper;

import com.ecms_backend.entity.Tag;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TagMapper {

    @Select("SELECT * FROM tags WHERE status = 1 ORDER BY sort_order ASC")
    List<Tag> selectAll();
}
