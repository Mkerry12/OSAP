package com.mqq.controller.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mqq.dto.TemplateDTO;
import com.mqq.result.PageResult;
import com.mqq.result.Result;
import com.mqq.service.TemplateService;
import com.mqq.vo.SurveyCopyVO;
import com.mqq.vo.TemplateDetailVO;
import com.mqq.vo.TemplateListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("adminTemplateController")
@RequestMapping("/admin/templates")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    @PostMapping
    public Result<TemplateDetailVO> createTemplate(@RequestBody TemplateDTO templateDTO) throws JsonProcessingException {
        log.info("创建问卷模板: title={}", templateDTO.getTitle());
        return templateService.createTemplate(templateDTO);
    }

    @GetMapping
    public Result<PageResult<TemplateListVO>> listTemplates(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "category", required = false) String category) {
        log.info("查询模板列表: page={}, size={}, category={}", page, size, category);
        PageResult<TemplateListVO> pageResult = templateService.listTemplates(page, size, category);
        return Result.success(pageResult);
    }

    @GetMapping("/{templateId}")
    public Result<TemplateDetailVO> getTemplateDetail(@PathVariable Long templateId) throws JsonProcessingException {
        log.info("获取模板详情: templateId={}", templateId);
        return templateService.getTemplateDetail(templateId);
    }

    @PutMapping("/{templateId}")
    public Result<TemplateDetailVO> updateTemplate(@PathVariable Long templateId,
                                                    @RequestBody TemplateDTO templateDTO) throws JsonProcessingException {
        log.info("更新模板: templateId={}", templateId);
        return templateService.updateTemplate(templateId, templateDTO);
    }

    @DeleteMapping("/{templateId}")
    public Result<Void> deleteTemplate(@PathVariable Long templateId) {
        log.info("删除模板: templateId={}", templateId);
        return templateService.deleteTemplate(templateId);
    }

    @PostMapping("/{templateId}/apply")
    public Result<SurveyCopyVO> applyTemplate(@PathVariable Long templateId,
                                               @RequestParam(value = "title", required = false) String title,
                                               @RequestParam(value = "description", required = false) String description) throws JsonProcessingException {
        log.info("应用模板创建问卷: templateId={}, title={}", templateId, title);
        return templateService.applyTemplate(templateId, title, description);
    }
}
