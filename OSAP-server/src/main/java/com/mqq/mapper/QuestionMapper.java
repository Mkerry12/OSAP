package com.mqq.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface QuestionMapper {

    @Select("select * from question where survey_id = #{surveyId} order by sort_order asc")
    java.util.List<com.mqq.entity.Question> getById(Long surveyId);
}
