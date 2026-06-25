package com.mqq.controller.user;

import com.mqq.dto.SubmitDTO;
import com.mqq.result.PageResult;
import com.mqq.result.Result;
import com.mqq.service.FillService;
import com.mqq.vo.MyAssignedSurveyVO;
import com.mqq.vo.MySubmissionRecordVO;
import com.mqq.vo.ResponseDetailVO;
import com.mqq.vo.ResponseListItemVO;
import com.mqq.vo.SubmitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("userFillController")
@RequestMapping("user/fill")
public class FillController {

    @Autowired
    private FillService fillService;

    @GetMapping("/surveys/{surveyId}/fill")
    public Result getFill(@PathVariable("surveyId") Long surveyId) {

        return fillService.getFill(surveyId);
    }

    @PostMapping("/surveys/{surveyId}/responses")
    public Result<SubmitVO> submitSurvey(@PathVariable("surveyId") Long surveyId,
                                         @RequestBody SubmitDTO submitDTO) {
        log.info("提交答卷: surveyId={}, answers={}, idempotencyKey={}",
                surveyId, submitDTO.getAnswers() != null ? submitDTO.getAnswers().size() : 0,
                submitDTO.getIdempotencyKey());
        return fillService.submitSurvey(surveyId, submitDTO);
    }

    @GetMapping("/surveys/{surveyId}/responses")
    public Result<PageResult<ResponseListItemVO>> getResponseList(
            @PathVariable("surveyId") Long surveyId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("获取答卷列表: surveyId={}, page={}, size={}", surveyId, page, size);
        PageResult<ResponseListItemVO> pageResult = fillService.getResponseList(surveyId, page, size);
        return Result.success(pageResult);
    }

    @GetMapping("/surveys/{surveyId}/responses/{responseId}")
    public Result<ResponseDetailVO> getResponseDetail(@PathVariable("surveyId") Long surveyId,
                                                       @PathVariable("responseId") Long responseId) {
        log.info("获取答卷详情: surveyId={}, responseId={}", surveyId, responseId);
        return fillService.getResponseDetail(surveyId, responseId);
    }

    @GetMapping("/surveys/my-assigned")
    public Result<PageResult<MyAssignedSurveyVO>> getMyAssigned(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("获取待填问卷列表: page={}, size={}", page, size);
        PageResult<MyAssignedSurveyVO> pageResult = fillService.getMyAssigned(page, size);
        return Result.success(pageResult);
    }

    @GetMapping("/surveys/my-submitted")
    public Result<PageResult<MySubmissionRecordVO>> getMySubmitted(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("获取已填问卷列表: page={}, size={}", page, size);
        PageResult<MySubmissionRecordVO> pageResult = fillService.getMySubmitted(page, size);
        return Result.success(pageResult);
    }
}
