package com.mqq.mapper;

import com.github.pagehelper.Page;
import com.mqq.entity.BackupRecord;
import org.apache.ibatis.annotations.*;

@Mapper
public interface BackupMapper {

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into backup_record(file_name, file_size, creator_id, create_at) " +
            "values (#{fileName}, #{fileSize}, #{creatorId}, #{createAt})")
    void insert(BackupRecord record);

    @Select("select * from backup_record order by create_at desc")
    Page<BackupRecord> pageQuery();

    @Select("select * from backup_record where id = #{id}")
    BackupRecord getById(Long id);

    @Delete("delete from backup_record where id = #{id}")
    void deleteById(Long id);
}
