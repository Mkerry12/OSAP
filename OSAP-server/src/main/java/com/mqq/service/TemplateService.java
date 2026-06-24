package com.mqq.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mqq.dto.TemplateDTO;
import com.mqq.result.PageResult;
import com.mqq.result.Result;
import com.mqq.vo.TemplateDetailVO;
import com.mqq.vo.TemplateListVO;
import com.mqq.vo.SurveyCopyVO;

public interface TemplateService {
    Result<TemplateDetailVO> createTemplate(TemplateDTO templateDTO) throws JsonProcessingException;
    PageResult<TemplateListVO> listTemplates(Integer page, Integer size, String category);
    Result<TemplateDetailVO> getTemplateDetail(Long templateId) throws JsonProcessingException;
    Result<TemplateDetailVO> updateTemplate(Long templateId, TemplateDTO templateDTO) throws JsonProcessingException;
    Result<Void> deleteTemplate(Long templateId);
    Result<SurveyCopyVO> applyTemplate(Long templateId, String title, String description) throws JsonProcessingException;
}
