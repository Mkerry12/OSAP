package com.mqq.controller.user;


import com.mqq.dto.PageQuerySurveyDTO;
import com.mqq.dto.SurveyDTO;
import com.mqq.result.PageResult;
import com.mqq.result.Result;
import com.mqq.service.SurveyService;
import com.mqq.vo.PageQuerySurveyVO;
import com.mqq.vo.SurveyVO;
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



}
