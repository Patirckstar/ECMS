package com.ecms_backend.mapper;

import com.ecms_backend.entity.AuditLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AuditLogMapper {

    @Insert("INSERT INTO audit_log (spu_id, audit_result, auditor_id, reject_reason, audit_time) " +
            "VALUES (#{spuId}, #{auditResult}, #{auditorId}, #{rejectReason}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AuditLog auditLog);

    @Select("SELECT * FROM audit_log WHERE spu_id = #{spuId} ORDER BY audit_time DESC")
    List<AuditLog> selectBySpuId(@Param("spuId") Long spuId);
}
