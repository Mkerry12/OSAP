package com.mqq.mapper;

import com.github.pagehelper.Page;
import com.mqq.dto.PageQuerySurveyDTO;
import com.mqq.entity.Survey;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SurveyMapper {

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into survey(title, description, type, target_phones, status, is_anonymous, allow_multi_submit, theme, start_time, end_time, question_count, response_count, creator_id, create_at, update_at) " +
            "values (#{title},#{description},#{type},#{targetPhones},#{status},#{isAnonymous},#{allowMultiSubmit},#{theme},#{startTime},#{endTime},#{questionCount},#{responseCount},#{creatorId},#{creationTime},#{updateTime})")
    void insert(Survey survey);

    Page<Survey> pageQuery(PageQuerySurveyDTO pageQuerySurveyDTO);

    @Select("select * from survey where id = #{id}")
    Survey getSurveyById(Long id);
}
