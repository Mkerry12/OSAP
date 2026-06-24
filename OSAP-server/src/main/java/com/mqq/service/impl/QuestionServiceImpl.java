package com.mqq.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.mqq.dto.QuestionDTO;
import com.mqq.dto.QuestionIdGroupDTO;
import com.mqq.dto.QuestionOptionDTO;
import com.mqq.entity.Question;
import com.mqq.entity.QuestionOption;
import com.mqq.mapper.QuestionMapper;
import com.mqq.mapper.QuestionOptionMapper;
import com.mqq.result.Result;
import com.mqq.service.QuestionService;
import com.mqq.vo.QuestionOptionVO;
import com.mqq.vo.QuestionVO;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class QuestionServiceImpl implements QuestionService {

    public static final String SUCCESS_ORDER_CHANGE = "更新排序成功";
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionOptionMapper questionOptionMapper;

    @Override
    public Result<QuestionVO> CreateQuestion(Long surveyId, QuestionDTO questionDTO) {

        Question question = BeanUtil.copyProperties(questionDTO, Question.class);
        question.setSurveyId(surveyId);
        questionMapper.insert(question);

        List<QuestionOptionDTO> questionOptions = questionDTO.getOptions();

        List<QuestionOption> questionOptionList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(questionOptions)) {
            for (QuestionOptionDTO questionOptionDTO : questionOptions) {
                QuestionOption questionOption = BeanUtil.copyProperties(questionOptionDTO, QuestionOption.class);
                questionOption.setQuestionId(question.getId());
                questionOption.setCreateAt(LocalDateTime.now());
                questionOptionList.add(questionOption);
            }
            questionOptionMapper.insertOptionsBatch(questionOptionList);
        }

        QuestionVO questionVO = BeanUtil.copyProperties(question, QuestionVO.class);

        List<QuestionOptionVO> questionOptionVOList = new ArrayList<>();
        for(QuestionOption questionOption : questionOptionList){
            QuestionOptionVO questionOptionVO = BeanUtil.copyProperties(questionOption, QuestionOptionVO.class);
            questionOptionVOList.add(questionOptionVO);
        }

        questionVO.setOptions(questionOptionVOList);

        return Result.success(questionVO);
    }

    @Override
    public Result<QuestionVO> UpdateQuestion(Long surveyId, Long questionId, QuestionDTO questionDTO) {

        Question question = questionMapper.selectBySurveyAndId(surveyId, questionId);
        if (question == null) {
            return Result.fail("题目不存在");
        }

        BeanUtil.copyProperties(questionDTO, question);
        question.setId(questionId);
        question.setSurveyId(surveyId);
        question.setUpdateAt(LocalDateTime.now());

        questionMapper.update(question);

        // 先删除旧选项（无论新选项是否为空，确保从 RADIO→TEXT 等切型不会残留数据）
        questionOptionMapper.deleteByQuestionId(questionId);

        // 再插入新选项（TEXT/RATING 题型不传 options，自然跳过）
        List<QuestionOptionDTO> optionDTOList = questionDTO.getOptions();
        if (CollectionUtil.isNotEmpty(optionDTOList)) {
            List<QuestionOption> optionList = new ArrayList<>();
            for (QuestionOptionDTO optionDTO : optionDTOList) {
                QuestionOption option = BeanUtil.copyProperties(optionDTO, QuestionOption.class);
                option.setQuestionId(questionId);
                option.setCreateAt(LocalDateTime.now());
                optionList.add(option);
            }
            questionOptionMapper.insertOptionsBatch(optionList);
        }

        // Build response VO
        QuestionVO questionVO = BeanUtil.copyProperties(question, QuestionVO.class);

        List<QuestionOptionVO> optionVOList = new ArrayList<>();
        List<QuestionOption> dbOptions = questionOptionMapper.getById(questionId);
        for (QuestionOption opt : dbOptions) {
            optionVOList.add(BeanUtil.copyProperties(opt, QuestionOptionVO.class));
        }
        questionVO.setOptions(optionVOList);

        return Result.success(questionVO);
    }

    @Override
    public Result<Void> deleteQuestion(Long surveyId, Long questionId) {

        Question question = questionMapper.selectBySurveyAndId(surveyId, questionId);
        if (question == null) {
            return Result.fail("题目不存在");
        }

        questionOptionMapper.deleteByQuestionId(questionId);
        questionMapper.deleteById(questionId);

        return Result.success();
    }

    @Override
    public Result QuestionOrder(Long surveyId, QuestionIdGroupDTO questionIdGroupDTO) {

        List<Long> questionIds = questionIdGroupDTO.getQuestionIds();
        if (CollectionUtil.isNotEmpty(questionIds)) {
            Integer SortOrder = 0;
            for (Long questionId : questionIds) {
                Question question = questionMapper.selectBySurveyAndId(surveyId, questionId);
                question.setSortOrder(SortOrder);
                question.setUpdateAt(LocalDateTime.now());
                questionMapper.update(question);
                SortOrder++;
            }
        }
        return Result.success(SUCCESS_ORDER_CHANGE);

    }
}
