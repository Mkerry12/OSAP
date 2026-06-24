package com.mqq.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mqq.UserHolder.UserHolder;
import com.mqq.constant.SurveyConstant;
import com.mqq.dto.QuestionDTO;
import com.mqq.dto.QuestionOptionDTO;
import com.mqq.dto.TemplateDTO;
import com.mqq.entity.*;
import com.mqq.mapper.*;
import com.mqq.result.PageResult;
import com.mqq.result.Result;
import com.mqq.service.TemplateService;
import com.mqq.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class TemplateServiceImpl implements TemplateService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private TemplateMapper templateMapper;

    @Autowired
    private SurveyMapper surveyMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionOptionMapper questionOptionMapper;

    @Autowired
    private UserMapper userMapper;

    private void checkAdmin() {
        UserInfo currentUser = UserHolder.getCurrentUser();
        User user = userMapper.getById(currentUser.getId());
        if (user == null || !"ADMIN".equals(user.getRole())) {
            throw new com.mqq.exception.BaseException("无权限操作");
        }
    }

    @Override
    public Result<TemplateDetailVO> createTemplate(TemplateDTO templateDTO) throws JsonProcessingException {
        checkAdmin();
        UserInfo userInfo = UserHolder.getCurrentUser();

        String questionsJson = templateDTO.getQuestions() != null
                ? objectMapper.writeValueAsString(templateDTO.getQuestions()) : null;

        SurveyTemplate template = SurveyTemplate.builder()
                .title(templateDTO.getTitle())
                .description(templateDTO.getDescription())
                .category(templateDTO.getCategory())
                .questions(questionsJson)
                .questionCount(templateDTO.getQuestions() != null ? templateDTO.getQuestions().size() : 0)
                .useCount(0)
                .creatorId(userInfo.getId())
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        templateMapper.insert(template);

        return Result.success(buildDetailVO(template, userInfo));
    }

    @Override
    public PageResult<TemplateListVO> listTemplates(Integer page, Integer size, String category) {
        checkAdmin();
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 10;

        PageHelper.startPage(page, size);
        Page<SurveyTemplate> pageResult = templateMapper.pageQuery(category);

        List<TemplateListVO> records = pageResult.getResult().stream().map(t -> {
            TemplateListVO vo = new TemplateListVO();
            BeanUtil.copyProperties(t, vo);
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(page, size, pageResult.getTotal(), records);
    }

    @Override
    public Result<TemplateDetailVO> getTemplateDetail(Long templateId) throws JsonProcessingException {
        checkAdmin();
        SurveyTemplate template = templateMapper.getById(templateId);
        if (template == null) {
            return Result.fail("模板不存在");
        }

        User creator = userMapper.getById(template.getCreatorId());
        UserInfo userInfo = creator != null
                ? new UserInfo(creator.getId(), creator.getUsername()) : null;

        return Result.success(buildDetailVO(template, userInfo));
    }

    @Override
    public Result<TemplateDetailVO> updateTemplate(Long templateId, TemplateDTO templateDTO) throws JsonProcessingException {
        checkAdmin();
        SurveyTemplate template = templateMapper.getById(templateId);
        if (template == null) {
            return Result.fail("模板不存在");
        }

        BeanUtil.copyProperties(templateDTO, template);
        template.setId(templateId);
        if (templateDTO.getQuestions() != null) {
            template.setQuestions(objectMapper.writeValueAsString(templateDTO.getQuestions()));
            template.setQuestionCount(templateDTO.getQuestions().size());
        }
        template.setUpdateAt(LocalDateTime.now());

        templateMapper.update(template);

        User creator = userMapper.getById(template.getCreatorId());
        UserInfo userInfo = creator != null
                ? new UserInfo(creator.getId(), creator.getUsername()) : null;

        return Result.success(buildDetailVO(templateMapper.getById(templateId), userInfo));
    }

    @Override
    public Result<Void> deleteTemplate(Long templateId) {
        checkAdmin();
        SurveyTemplate template = templateMapper.getById(templateId);
        if (template == null) {
            return Result.fail("模板不存在");
        }
        templateMapper.deleteById(templateId);
        return Result.success();
    }

    @Override
    public Result<SurveyCopyVO> applyTemplate(Long templateId, String title, String description) throws JsonProcessingException {
        checkAdmin();
        SurveyTemplate template = templateMapper.getById(templateId);
        if (template == null) {
            return Result.fail("模板不存在");
        }

        UserInfo userInfo = UserHolder.getCurrentUser();

        String surveyTitle = (title != null && !title.isBlank()) ? title : template.getTitle();
        String surveyDescription = (description != null && !description.isBlank())
                ? description : template.getDescription();

        Survey survey = Survey.builder()
                .title(surveyTitle)
                .description(surveyDescription)
                .type(SurveyConstant.TYPE_PUBLIC)
                .status(SurveyConstant.STATUS_DRAFT)
                .isAnonymous(SurveyConstant.NO_ANONYMOUS)
                .allowMultiSubmit(SurveyConstant.ALLOW_MULTI_SUBMIT_NO_SUBMIT)
                .questionCount(0)
                .responseCount(0)
                .creatorId(userInfo.getId())
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        surveyMapper.insert(survey);

        List<QuestionDTO> questionDTOs = objectMapper.readValue(template.getQuestions(),
                new TypeReference<List<QuestionDTO>>() {});
        if (questionDTOs != null) {
            for (QuestionDTO qDTO : questionDTOs) {
                Question question = BeanUtil.copyProperties(qDTO, Question.class);
                question.setId(null);
                question.setSurveyId(survey.getId());
                question.setCreateAt(LocalDateTime.now());
                question.setUpdateAt(LocalDateTime.now());
                questionMapper.insert(question);

                if (qDTO.getOptions() != null) {
                    List<QuestionOption> optionList = new ArrayList<>();
                    for (QuestionOptionDTO oDTO : qDTO.getOptions()) {
                        QuestionOption option = BeanUtil.copyProperties(oDTO, QuestionOption.class);
                        option.setQuestionId(question.getId());
                        option.setCreateAt(LocalDateTime.now());
                        optionList.add(option);
                    }
                    if (!optionList.isEmpty()) {
                        questionOptionMapper.insertOptionsBatch(optionList);
                    }
                }
            }
            survey.setQuestionCount(questionDTOs.size());
            surveyMapper.update(survey);
        }

        templateMapper.incrementUseCount(templateId);

        SurveyCopyVO vo = new SurveyCopyVO(
                survey.getId(), survey.getTitle(), survey.getStatus(),
                survey.getQuestionCount(), survey.getResponseCount(),
                survey.getCreateAt()
        );
        return Result.success(vo);
    }

    private TemplateDetailVO buildDetailVO(SurveyTemplate template, UserInfo userInfo) throws JsonProcessingException {
        TemplateDetailVO vo = new TemplateDetailVO();
        BeanUtil.copyProperties(template, vo);

        CreatorVO creatorVO = userInfo != null
                ? new CreatorVO(userInfo.getId(), userInfo.getUsername()) : null;
        vo.setCreator(creatorVO);

        if (template.getQuestions() != null) {
            List<QuestionDTO> questionDTOs = objectMapper.readValue(template.getQuestions(),
                new TypeReference<List<QuestionDTO>>() {});
            if (questionDTOs != null) {
                List<QuestionVO> questionVOs = questionDTOs.stream().map(q -> {
                    QuestionVO qVO = BeanUtil.copyProperties(q, QuestionVO.class);
                    if (q.getOptions() != null) {
                        qVO.setOptions(q.getOptions().stream().map(o -> {
                            QuestionOptionVO oVO = new QuestionOptionVO();
                            BeanUtil.copyProperties(o, oVO);
                            return oVO;
                        }).collect(Collectors.toList()));
                    }
                    return qVO;
                }).collect(Collectors.toList());
                vo.setQuestions(questionVOs);
            }
        }

        return vo;
    }
}
