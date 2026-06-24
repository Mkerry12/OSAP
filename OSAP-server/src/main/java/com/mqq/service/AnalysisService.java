package com.mqq.service;

import com.mqq.result.Result;
import com.mqq.vo.QuestionAnalysisVO;
import com.mqq.vo.SurveyOverviewVO;
import com.mqq.vo.WordCloudVO;

import java.io.IOException;
import java.io.OutputStream;

public interface AnalysisService {
    Result<SurveyOverviewVO> getOverview(Long surveyId);
    Result<QuestionAnalysisVO> getQuestionAnalysis(Long surveyId);
    Result<WordCloudVO> getWordCloud(Long surveyId);
    void export(Long surveyId, String format, OutputStream outputStream) throws IOException;
}
