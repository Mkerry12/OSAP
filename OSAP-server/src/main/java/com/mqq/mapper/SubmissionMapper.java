package com.mqq.mapper;

import com.mqq.entity.Submission;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface SubmissionMapper {

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into submission(survey_id, user_id, idempotency_key, duration, submit_at) " +
            "values (#{surveyId}, #{userId}, #{idempotencyKey}, #{duration}, #{submitAt})")
    void insert(Submission submission);

    @Select("select count(*) from submission where survey_id = #{surveyId} and user_id = #{userId}")
    int countBySurveyAndUser(@Param("surveyId") Long surveyId, @Param("userId") Long userId);

    @Select("select * from submission where idempotency_key = #{key}")
    Submission getByIdempotencyKey(String key);

    @Select("select * from submission where id = #{id}")
    Submission getById(Long id);

    @Select("select * from submission where id = #{responseId} and survey_id = #{surveyId}")
    Submission getByIdAndSurveyId(@Param("responseId") Long responseId, @Param("surveyId") Long surveyId);

    @Select("select count(*) from submission where survey_id = #{surveyId}")
    int countBySurveyId(Long surveyId);

    @Select("select round(avg(duration)) from submission where survey_id = #{surveyId} and duration is not null")
    Integer averageDurationBySurveyId(Long surveyId);

    @Select("select date(submit_at) as `date`, count(*) as `count` from submission " +
            "where survey_id = #{surveyId} group by date(submit_at) order by `date`")
    List<Map<String, Object>> dailyResponseCounts(Long surveyId);

    @Select("select s.id, s.submit_at, s.duration, u.username as respondent " +
            "from submission s left join user u on s.user_id = u.id " +
            "where s.survey_id = #{surveyId} order by s.submit_at desc")
    List<Map<String, Object>> pageBySurveyId(Long surveyId);

    @Select("select * from submission where survey_id = #{surveyId}")
    List<Submission> listBySurveyId(Long surveyId);

    @Delete("delete from submission where survey_id = #{surveyId}")
    void deleteBySurveyId(Long surveyId);
}
