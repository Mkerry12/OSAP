package com.mqq.service;

import com.mqq.dto.QuestionDTO;
import com.mqq.dto.QuestionIdGroupDTO;
import com.mqq.result.Result;
import com.mqq.vo.QuestionVO;

public interface QuestionService {
    Result<QuestionVO> CreateQuestion(Long surveyId, QuestionDTO questionDTO);

    Result<QuestionVO> UpdateQuestion(Long surveyId, Long questionId, QuestionDTO questionDTO);

    Result<Void> deleteQuestion(Long surveyId, Long questionId);

    Result QuestionOrder(Long surveyId, QuestionIdGroupDTO questionDTO);
}
