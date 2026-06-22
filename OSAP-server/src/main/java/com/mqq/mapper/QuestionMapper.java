package com.mqq.mapper;

import com.mqq.entity.Question;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionMapper {

    @Select("select * from question where survey_id = #{surveyId} order by sort_order asc")
    List<Question> getById(Long surveyId);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into question(survey_id, type, title, required, sort_order, min_rating, max_rating, create_at, update_at) " +
            "values (#{surveyId},#{type},#{title},#{required},#{sortOrder},#{minRating},#{maxRating},#{createAt},#{updateAt})")
    void insert(Question question);

    @Delete("delete from question where survey_id = #{surveyId}")
    void deleteBySurveyId(Long surveyId);
}
