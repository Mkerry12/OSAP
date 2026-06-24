package com.mqq.mapper;

import com.github.pagehelper.Page;
import com.mqq.entity.SurveyTemplate;
import org.apache.ibatis.annotations.*;

@Mapper
public interface TemplateMapper {

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into survey_template(title, description, category, questions, question_count, use_count, creator_id, create_at, update_at) " +
            "values (#{title}, #{description}, #{category}, #{questions}, #{questionCount}, #{useCount}, #{creatorId}, #{createAt}, #{updateAt})")
    void insert(SurveyTemplate template);

    @Select("select * from survey_template where id = #{id}")
    SurveyTemplate getById(Long id);

    void update(SurveyTemplate template);

    @Delete("delete from survey_template where id = #{id}")
    void deleteById(Long id);

    Page<SurveyTemplate> pageQuery(@Param("category") String category);

    @Update("update survey_template set use_count = use_count + 1 where id = #{id}")
    void incrementUseCount(Long id);
}
