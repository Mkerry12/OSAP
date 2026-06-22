package com.mqq.mapper;

import com.mqq.entity.QuestionOption;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionOptionMapper {

    @Select("select * from question_option where question_id = #{questionId} order by sort_order asc")
    List<QuestionOption> getById(Long questionId);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into question_option(question_id, label, sort_order, create_at) " +
            "values (#{questionId},#{label},#{sortOrder},#{createAt})")
    void insert(QuestionOption questionOption);

    @Delete("delete from question_option where question_id = #{questionId}")
    void deleteByQuestionId(Long questionId);
}
