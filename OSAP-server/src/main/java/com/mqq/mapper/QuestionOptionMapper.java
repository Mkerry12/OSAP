package com.mqq.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface QuestionOptionMapper {

    @Select("select * from question_option where question_id = #{questionId} order by sort_order asc")
    java.util.List<com.mqq.entity.QuestionOption> getById(Long questionId);
}
