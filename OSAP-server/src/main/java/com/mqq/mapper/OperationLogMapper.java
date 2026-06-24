package com.mqq.mapper;

import com.github.pagehelper.Page;
import com.mqq.entity.OperationLog;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OperationLogMapper {

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into operation_log(type, operator, action, target, ip, detail, create_at) " +
            "values (#{type}, #{operator}, #{action}, #{target}, #{ip}, #{detail}, #{createAt})")
    void insert(OperationLog log);

    Page<OperationLog> pageQuery(@Param("type") String type,
                                  @Param("startTime") String startTime,
                                  @Param("endTime") String endTime,
                                  @Param("keyword") String keyword);
}
