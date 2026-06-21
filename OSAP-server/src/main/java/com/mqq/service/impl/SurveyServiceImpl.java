package com.mqq.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.mqq.UserHolder.UserHolder;
import com.mqq.constant.SurveyConstant;
import com.mqq.dto.SurveyDTO;
import com.mqq.entity.Survey;
import com.mqq.entity.UserInfo;
import com.mqq.mapper.SurveyMapper;
import com.mqq.result.Result;
import com.mqq.service.SurveyService;
import com.mqq.vo.SurveyVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class SurveyServiceImpl implements SurveyService {

    @Autowired
    private SurveyMapper surveyMapper;

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
}
