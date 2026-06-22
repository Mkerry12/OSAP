package com.mqq.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mqq.UserHolder.UserHolder;
import com.mqq.constant.SurveyConstant;
import com.mqq.dto.PageQuerySurveyDTO;
import com.mqq.dto.SurveyDTO;
import com.mqq.entity.*;
import com.mqq.mapper.QuestionMapper;
import com.mqq.mapper.QuestionOptionMapper;
import com.mqq.mapper.SurveyMapper;
import com.mqq.mapper.UserMapper;
import com.mqq.result.PageResult;
import com.mqq.result.Result;
import com.mqq.service.SurveyService;
import com.mqq.vo.*;
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
public class SurveyServiceImpl implements SurveyService {

    @Autowired
    private SurveyMapper surveyMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionOptionMapper questionOptionMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result<SurveyVO> CreateSurvey(SurveyDTO surveyDTO) {

        UserInfo userInfo = UserHolder.getCurrentUser();

        Survey survey = BeanUtil.copyProperties(surveyDTO, Survey.class);
        survey.setStatus(SurveyConstant.STATUS_DRAFT);
        survey.setCreatorId(userInfo.getId());
        survey.setCreationTime(LocalDateTime.now());
        survey.setUpdateTime(LocalDateTime.now());

        surveyMapper.insert(survey);

        SurveyVO surveyVO = BeanUtil.copyProperties(survey, SurveyVO.class);
        surveyVO.setCreator(userInfo);

        return Result.success(surveyVO);
    }

    @Override
    public PageResult pageQuerySurveys(PageQuerySurveyDTO pageQuerySurveyDTO) {

        Integer pageNum = pageQuerySurveyDTO.getPage();
        Integer sizeNum = pageQuerySurveyDTO.getSize();

        PageHelper.startPage(pageNum, sizeNum);

        Page<Survey> page = surveyMapper.pageQuery(pageQuerySurveyDTO);

        List<PageQuerySurveyVO> pageQuerySurveyVOList = new ArrayList<>();

        List<Survey> surveyList = page.getResult();

        for (Survey survey : surveyList) {
            PageQuerySurveyVO pageQuerySurveyVO = new PageQuerySurveyVO();
            BeanUtil.copyProperties(survey, pageQuerySurveyVO);
            pageQuerySurveyVOList.add(pageQuerySurveyVO);
        }
        return new PageResult(pageNum,sizeNum,page.getTotal(),pageQuerySurveyVOList);

    }

    @Override
    public Result<SurveyVO> GetSurveyDetails(Long surveyId) {

        Survey survey = surveyMapper.getSurveyById(surveyId);

        SurveyDetailsVO surveyDetailsVO = BeanUtil.copyProperties(survey, SurveyDetailsVO.class);

        //查该表关联的UserInfo
        Long creatorId = survey.getCreatorId();
        User user = userMapper.getById(creatorId);
        UserInfo userInfo = BeanUtil.copyProperties(user, UserInfo.class);
        surveyDetailsVO.setCreator(userInfo);

        List<Question> questionList = questionMapper.getById(surveyId);
        List<QuestionVO> questionVOList = new ArrayList<>();

        for(Question question : questionList){

            QuestionVO questionVO = BeanUtil.copyProperties(question, QuestionVO.class);
            Long questionId = question.getId();
            List<QuestionOption> questionOptionList = questionOptionMapper.getById(questionId);
            List<QuestionOptionVO> questionOptionVOList = new ArrayList<>();
            for(QuestionOption questionOption : questionOptionList){
                QuestionOptionVO questionOptionVO = BeanUtil.copyProperties(questionOption, QuestionOptionVO.class);
                questionOptionVOList.add(questionOptionVO);
            }
            questionVO.setOptions(questionOptionVOList);
            questionVOList.add(questionVO);
        }
        surveyDetailsVO.setQuestionList(questionVOList);

        return Result.success(surveyDetailsVO);
    }

}
