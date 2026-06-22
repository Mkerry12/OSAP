package com.mqq.service;

import com.mqq.dto.PageQuerySurveyDTO;
import com.mqq.dto.SurveyDTO;
import com.mqq.result.PageResult;
import com.mqq.result.Result;
import com.mqq.vo.PageQuerySurveyVO;
import com.mqq.vo.SurveyVO;

public interface SurveyService {
    Result<SurveyVO> CreateSurvey(SurveyDTO surveyDTO);

    PageResult<PageQuerySurveyVO> pageQuerySurveys(PageQuerySurveyDTO pageQuerySurveyDTO);

    Result<SurveyVO> GetSurveyDetails(Long surveyId);
}
