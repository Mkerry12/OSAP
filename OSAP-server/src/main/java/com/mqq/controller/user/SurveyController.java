package com.mqq.controller.user;


import com.mqq.dto.PageQuerySurveyDTO;
import com.mqq.dto.SurveyDTO;
import com.mqq.dto.SurveyUpdateDTO;
import com.mqq.result.PageResult;
import com.mqq.result.Result;
import com.mqq.service.SurveyService;
import com.mqq.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/Survey")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @PostMapping("/surveys")
    public Result<SurveyVO> CreateSurvey(@RequestBody SurveyDTO surveyDTO) {
        return surveyService.CreateSurvey(surveyDTO);
    }

    @GetMapping("/surveys")
    public Result<PageResult<PageQuerySurveyVO>> GetSurveysByPage(@RequestParam PageQuerySurveyDTO pageQuerySurveyDTO) {

        PageResult<PageQuerySurveyVO> pageResult = surveyService.pageQuerySurveys(pageQuerySurveyDTO);

        return Result.success(pageResult);
    }

    @GetMapping("/surveys/{surveyId}")
    public Result<SurveyVO> GetSurveyById(@PathVariable("surveyId") Long surveyId) {

        return surveyService.GetSurveyDetails(surveyId);
    }

    @PutMapping("/surveys/{surveyId}")
    public Result<SurveyUpdateVO> UpdateSurvey(@PathVariable("surveyId") Long surveyId,
                                               @RequestBody SurveyUpdateDTO updateDTO) {
        return surveyService.updateSurvey(surveyId, updateDTO);
    }

    @DeleteMapping("/surveys/{surveyId}")
    public Result<Void> DeleteSurvey(@PathVariable("surveyId") Long surveyId) {
        return surveyService.deleteSurvey(surveyId);
    }

    @PutMapping("/surveys/{surveyId}/publish")
    public Result<SurveyStatusVO> PublishSurvey(@PathVariable("surveyId") Long surveyId) {
        return surveyService.publishSurvey(surveyId);
    }

    @PutMapping("/surveys/{surveyId}/close")
    public Result<SurveyStatusVO> CloseSurvey(@PathVariable("surveyId") Long surveyId) {
        return surveyService.closeSurvey(surveyId);
    }

    @PostMapping("/surveys/{surveyId}/copy")
    public Result<SurveyCopyVO> CopySurvey(@PathVariable("surveyId") Long surveyId) {
        return surveyService.copySurvey(surveyId);
    }

    @GetMapping("/surveys/{surveyId}/preview")
    public Result<SurveyPreviewVO> PreviewSurvey(@PathVariable("surveyId") Long surveyId) {
        return surveyService.previewSurvey(surveyId);
    }
}
