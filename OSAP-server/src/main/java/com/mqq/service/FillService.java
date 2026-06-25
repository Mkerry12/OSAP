package com.mqq.service;

import com.mqq.dto.SubmitDTO;
import com.mqq.result.PageResult;
import com.mqq.result.Result;
import com.mqq.vo.MyAssignedSurveyVO;
import com.mqq.vo.MySubmissionRecordVO;
import com.mqq.vo.ResponseDetailVO;
import com.mqq.vo.ResponseListItemVO;
import com.mqq.vo.SubmitVO;

public interface FillService {
    Result getFill(Long surveyId);
    Result<SubmitVO> submitSurvey(Long surveyId, SubmitDTO submitDTO);
    Result<ResponseDetailVO> getResponseDetail(Long surveyId, Long responseId);
    PageResult<ResponseListItemVO> getResponseList(Long surveyId, Integer page, Integer size);
    PageResult<MyAssignedSurveyVO> getMyAssigned(Integer page, Integer size);
    PageResult<MySubmissionRecordVO> getMySubmitted(Integer page, Integer size);
}
