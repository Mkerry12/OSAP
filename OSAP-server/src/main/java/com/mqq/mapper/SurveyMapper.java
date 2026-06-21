package com.mqq.mapper;

import com.mqq.entity.Survey;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface SurveyMapper {

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into survey(title, description, type, target_phones, status, is_anonymous, allow_multi_submit, theme, start_time, end_time, question_count, response_count, creator_id, create_at, update_at) " +
            "values (#{title},#{description},#{type},#{targetPhones},#{status},#{isAnonymous},#{allowMultiSumbit},#{theme},#{startTime},#{endTime},#{questionCount},#{responseCount},#{creatorId},#{createAt},#{updateAt})")
    void insert(Survey survey);
}
