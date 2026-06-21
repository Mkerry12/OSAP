package com.mqq.service;

import com.mqq.dto.SurveyDTO;
import com.mqq.result.Result;
import com.mqq.vo.SurveyVO;

public interface SurveyService {
    Result<SurveyVO> CreateSurvey(SurveyDTO surveyDTO);
}
