package com.mqq.service;

import com.mqq.dto.PageQuerySurveyDTO;
import com.mqq.dto.SurveyDTO;
import com.mqq.dto.SurveyUpdateDTO;
import com.mqq.result.PageResult;
import com.mqq.result.Result;
import com.mqq.vo.*;

public interface SurveyService {
    Result<SurveyVO> CreateSurvey(SurveyDTO surveyDTO);

    PageResult<PageQuerySurveyVO> pageQuerySurveys(PageQuerySurveyDTO pageQuerySurveyDTO);

    Result<SurveyVO> GetSurveyDetails(Long surveyId);

    Result<SurveyUpdateVO> updateSurvey(Long surveyId, SurveyUpdateDTO updateDTO);

    Result<Void> deleteSurvey(Long surveyId);

    Result<SurveyStatusVO> publishSurvey(Long surveyId);

    Result<SurveyStatusVO> closeSurvey(Long surveyId);

    Result<SurveyCopyVO> copySurvey(Long surveyId);

    Result<SurveyPreviewVO> previewSurvey(Long surveyId);
}
