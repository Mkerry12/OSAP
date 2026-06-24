package com.mqq.controller.user;

import com.mqq.dto.QuestionDTO;
import com.mqq.dto.QuestionIdGroupDTO;
import com.mqq.result.Result;
import com.mqq.service.QuestionService;
import com.mqq.vo.QuestionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @PostMapping("/surveys/{surveyId}/questions")
    public Result<QuestionVO> CreateQuestion(@PathVariable Long surveyId, @RequestBody QuestionDTO  questionDTO) {
        log.info("创建题目: surveyId={}", surveyId);
        return questionService.CreateQuestion(surveyId,questionDTO);

    }

    @PutMapping("/surveys/{surveyId}/questions/{questionId}")
    public Result<QuestionVO> UpdateQuestion(@PathVariable Long surveyId, @PathVariable Long questionId, @RequestBody QuestionDTO  questionDTO) {
        log.info("更新题目: surveyId={}, questionId={}", surveyId, questionId);
        return questionService.UpdateQuestion(surveyId,questionId,questionDTO);

    }

    @DeleteMapping("/surveys/{surveyId}/questions/{questionId}")
    public Result<Void> DeleteQuestion(@PathVariable Long surveyId, @PathVariable Long questionId) {
        log.info("删除题目: surveyId={}, questionId={}", surveyId, questionId);
        return questionService.deleteQuestion(surveyId,questionId);

    }

    @PutMapping("/surveys/{surveyId}/questions/order")
    public Result QuestionOrder(@PathVariable Long surveyId, @RequestBody QuestionIdGroupDTO questionIdGroupDTO) {
        log.info("更新题目: surveyId={}, questionDTO={}", surveyId, questionIdGroupDTO);
        return questionService.QuestionOrder(surveyId,questionIdGroupDTO);
    }

}
