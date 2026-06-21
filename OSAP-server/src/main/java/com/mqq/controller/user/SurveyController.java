package com.mqq.controller.user;


import com.mqq.dto.SurveyDTO;
import com.mqq.result.Result;
import com.mqq.service.SurveyService;
import com.mqq.vo.SurveyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/Survey")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @PostMapping("/surveys")
    public Result<SurveyVO> CreateSurvey(@RequestBody SurveyDTO surveyDTO) {
        return surveyService.CreateSurvey(surveyDTO);
    }


}
