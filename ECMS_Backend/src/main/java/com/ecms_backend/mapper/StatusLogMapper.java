package com.ecms_backend.mapper;

import com.ecms_backend.entity.StatusLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StatusLogMapper {

    @Insert("INSERT INTO status_log (spu_id, from_status, to_status, operator_id, remark, created_at) " +
            "VALUES (#{spuId}, #{fromStatus}, #{toStatus}, #{operatorId}, #{remark}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(StatusLog statusLog);

    @Select("SELECT * FROM status_log WHERE spu_id = #{spuId} ORDER BY created_at DESC")
    List<StatusLog> selectBySpuId(@Param("spuId") Long spuId);
}
