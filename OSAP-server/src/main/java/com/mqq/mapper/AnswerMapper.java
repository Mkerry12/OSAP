package com.mqq.mapper;

import com.mqq.entity.Answer;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface AnswerMapper {

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into answer(submission_id, question_id, value) " +
            "values (#{submissionId}, #{questionId}, #{value})")
    void insert(Answer answer);

    void insertBatch(List<Answer> answers);

    @Select("select * from answer where submission_id = #{submissionId}")
    List<Answer> getBySubmissionId(Long submissionId);

    @Select("select count(*) from answer a join submission s on a.submission_id = s.id " +
            "where s.survey_id = #{surveyId} and a.question_id = #{questionId}")
    int countBySurveyAndQuestion(@Param("surveyId") Long surveyId, @Param("questionId") Long questionId);

    @Select("select a.value, count(*) as `count` from answer a " +
            "join submission s on a.submission_id = s.id " +
            "where s.survey_id = #{surveyId} and a.question_id = #{questionId} " +
            "group by a.value order by `count` desc")
    List<Map<String, Object>> getValueDistribution(@Param("surveyId") Long surveyId, @Param("questionId") Long questionId);

    @Select("select a.value from answer a " +
            "join submission s on a.submission_id = s.id " +
            "join question q on a.question_id = q.id " +
            "where s.survey_id = #{surveyId} and q.type = 'TEXT'")
    List<String> getTextAnswers(Long surveyId);

    @Select("select count(*) from answer a join submission s on a.submission_id = s.id " +
            "where s.survey_id = #{surveyId}")
    int countAllBySurveyId(Long surveyId);
}
