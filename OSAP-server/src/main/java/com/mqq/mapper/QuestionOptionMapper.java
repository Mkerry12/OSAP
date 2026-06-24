package com.mqq.mapper;

import com.mqq.annotation.AutoFill;
import com.mqq.entity.QuestionOption;
import com.mqq.enumeration.OperationType;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionOptionMapper {

    @Select("select * from question_option where question_id = #{questionId} order by sort_order asc")
    List<QuestionOption> getById(Long questionId);

    @AutoFill(OperationType.INSERT)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into question_option(question_id, label, sort_order, create_at) " +
            "values (#{questionId},#{label},#{sortOrder},#{createAt})")
    void insert(QuestionOption questionOption);

    @Delete("delete from question_option where question_id = #{questionId}")
    void deleteByQuestionId(Long questionId);

    @Options(useGeneratedKeys = true,keyProperty = "id")
    void insertOptionsBatch(List<QuestionOption> questionOptionList);

    @Select({"<script>",
             "select * from question_option where question_id in ",
             "<foreach collection='questionIds' item='id' open='(' separator=',' close=')'>",
             "#{id}",
             "</foreach>",
             "order by sort_order asc",
             "</script>"})
    List<QuestionOption> getByQuestionIds(@Param("questionIds") List<Long> questionIds);
}
