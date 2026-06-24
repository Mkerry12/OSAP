package com.mqq.controller.user;


import com.mqq.dto.PageQuerySurveyDTO;
import com.mqq.dto.SurveyDTO;
import com.mqq.dto.SurveyUpdateDTO;
import com.mqq.result.PageResult;
import com.mqq.result.Result;
import com.mqq.service.SurveyService;
import com.mqq.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user/survey")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @PostMapping("/surveys")
    public Result<SurveyVO> CreateSurvey(@RequestBody SurveyDTO surveyDTO) {
        log.info("创建问卷");
        return surveyService.CreateSurvey(surveyDTO);
    }

    @GetMapping("/surveys")
    public Result<PageResult<PageQuerySurveyVO>> GetSurveysByPage(@RequestParam PageQuerySurveyDTO pageQuerySurveyDTO) {
        log.info("分页查询问卷");
        PageResult<PageQuerySurveyVO> pageResult = surveyService.pageQuerySurveys(pageQuerySurveyDTO);

        return Result.success(pageResult);
    }

    @GetMapping("/surveys/{surveyId}")
    public Result<SurveyVO> GetSurveyById(@PathVariable("surveyId") Long surveyId) {
        log.info("查询问卷详情: {}", surveyId);
        return surveyService.GetSurveyDetails(surveyId);
    }

    @PutMapping("/surveys/{surveyId}")
    public Result<SurveyUpdateVO> UpdateSurvey(@PathVariable("surveyId") Long surveyId,
                                               @RequestBody SurveyUpdateDTO updateDTO) {
        log.info("更新问卷: {}", surveyId);
        return surveyService.updateSurvey(surveyId, updateDTO);
    }

    @DeleteMapping("/surveys/{surveyId}")
    public Result<Void> DeleteSurvey(@PathVariable("surveyId") Long surveyId) {
        log.info("删除问卷: {}", surveyId);
        return surveyService.deleteSurvey(surveyId);
    }

    @PutMapping("/surveys/{surveyId}/publish")
    public Result<SurveyStatusVO> PublishSurvey(@PathVariable("surveyId") Long surveyId) {
        log.info("发布问卷: {}", surveyId);
        return surveyService.publishSurvey(surveyId);
    }

    @PutMapping("/surveys/{surveyId}/close")
    public Result<SurveyStatusVO> CloseSurvey(@PathVariable("surveyId") Long surveyId) {
        log.info("关闭问卷: {}", surveyId);
        return surveyService.closeSurvey(surveyId);
    }

    @PostMapping("/surveys/{surveyId}/copy")
    public Result<SurveyCopyVO> CopySurvey(@PathVariable("surveyId") Long surveyId) {
        log.info("复制问卷: {}", surveyId);
        return surveyService.copySurvey(surveyId);
    }

    @GetMapping("/surveys/{surveyId}/preview")
    public Result<SurveyPreviewVO> PreviewSurvey(@PathVariable("surveyId") Long surveyId) {
        log.info("预览问卷: {}", surveyId);
        return surveyService.previewSurvey(surveyId);
    }

    @GetMapping("/templates")
    public Result<PageResult<TemplateListVO>> listTemplates(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("查询模板列表: page={}, size={}", page, size);
        PageResult<TemplateListVO> pageResult = surveyService.listTemplates(page, size);
        return Result.success(pageResult);
    }

    @PostMapping("/templates/{templateId}/apply")
    public Result<SurveyCopyVO> applyTemplate(
            @PathVariable Long templateId,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description) {
        log.info("应用模板创建问卷: templateId={}, title={}", templateId, title);
        return surveyService.applyTemplate(templateId, title, description);
    }
}
