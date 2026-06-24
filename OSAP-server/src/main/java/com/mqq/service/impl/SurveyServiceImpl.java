package com.mqq.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mqq.UserHolder.UserHolder;
import com.mqq.constant.SurveyConstant;
import com.mqq.dto.PageQuerySurveyDTO;
import com.mqq.dto.QuestionDTO;
import com.mqq.dto.QuestionOptionDTO;
import com.mqq.dto.SurveyDTO;
import com.mqq.dto.SurveyUpdateDTO;
import com.mqq.entity.*;
import com.mqq.mapper.QuestionMapper;
import com.mqq.mapper.QuestionOptionMapper;
import com.mqq.mapper.SurveyMapper;
import com.mqq.mapper.TemplateMapper;
import com.mqq.mapper.UserMapper;
import com.mqq.result.PageResult;
import com.mqq.result.Result;
import com.mqq.service.SurveyService;
import com.mqq.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class SurveyServiceImpl implements SurveyService {

    @Autowired
    private SurveyMapper surveyMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionOptionMapper questionOptionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TemplateMapper templateMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Result<SurveyVO> CreateSurvey(SurveyDTO surveyDTO) {

        UserInfo userInfo = UserHolder.getCurrentUser();

        Survey survey = BeanUtil.copyProperties(surveyDTO, Survey.class);
        survey.setStatus(SurveyConstant.STATUS_DRAFT);
        if (survey.getIsAnonymous() == null) survey.setIsAnonymous(0);
        if (survey.getAllowMultiSubmit() == null) survey.setAllowMultiSubmit(0);
        surveyMapper.insert(survey);

        SurveyVO surveyVO = BeanUtil.copyProperties(survey, SurveyVO.class);
        surveyVO.setCreator(userInfo);

        return Result.success(surveyVO);
    }

    @Override
    public PageResult pageQuerySurveys(PageQuerySurveyDTO pageQuerySurveyDTO) {

        Integer pageNum = pageQuerySurveyDTO.getPage();
        Integer sizeNum = pageQuerySurveyDTO.getSize();

        UserInfo userInfo = UserHolder.getCurrentUser();
        pageQuerySurveyDTO.setCreateId(userInfo.getId());

        PageHelper.startPage(pageNum, sizeNum);


        Page<Survey> page = surveyMapper.pageQuery(pageQuerySurveyDTO);

        List<PageQuerySurveyVO> pageQuerySurveyVOList = new ArrayList<>();

        List<Survey> surveyList = page.getResult();

        for (Survey survey : surveyList) {
            PageQuerySurveyVO pageQuerySurveyVO = new PageQuerySurveyVO();
            BeanUtil.copyProperties(survey, pageQuerySurveyVO);
            pageQuerySurveyVOList.add(pageQuerySurveyVO);
        }
        return new PageResult<>(pageNum,sizeNum,page.getTotal(),pageQuerySurveyVOList);

    }

    @Override
    public Result<SurveyVO> GetSurveyDetails(Long surveyId) {

        Survey survey = surveyMapper.getSurveyById(surveyId);

        SurveyDetailsVO surveyDetailsVO = BeanUtil.copyProperties(survey, SurveyDetailsVO.class);

        //查该表关联的UserInfo
        Long creatorId = survey.getCreatorId();
        User user = userMapper.getById(creatorId);
        UserInfo userInfo = BeanUtil.copyProperties(user, UserInfo.class);
        surveyDetailsVO.setCreator(userInfo);

        List<Question> questionList = questionMapper.getById(surveyId);
        List<QuestionVO> questionVOList = new ArrayList<>();

        for(Question question : questionList){

            QuestionVO questionVO = BeanUtil.copyProperties(question, QuestionVO.class);
            Long questionId = question.getId();
            List<QuestionOption> questionOptionList = questionOptionMapper.getById(questionId);
            List<QuestionOptionVO> questionOptionVOList = new ArrayList<>();
            for(QuestionOption questionOption : questionOptionList){
                QuestionOptionVO questionOptionVO = BeanUtil.copyProperties(questionOption, QuestionOptionVO.class);
                questionOptionVOList.add(questionOptionVO);
            }
            questionVO.setOptions(questionOptionVOList);
            questionVOList.add(questionVO);
        }
        surveyDetailsVO.setQuestionList(questionVOList);

        return Result.success(surveyDetailsVO);
    }

    @Override
    public Result<SurveyUpdateVO> updateSurvey(Long surveyId, SurveyUpdateDTO updateDTO) {

        Survey survey = surveyMapper.getSurveyById(surveyId);
        if (survey == null) {
            return Result.fail("问卷不存在");
        }

        BeanUtil.copyProperties(updateDTO, survey);
        survey.setId(surveyId);
        surveyMapper.update(survey);

        SurveyUpdateVO vo = new SurveyUpdateVO(surveyId, survey.getTitle(), survey.getUpdateAt());
        return Result.success(vo);
    }

    @Override
    public Result<Void> deleteSurvey(Long surveyId) {

        Survey survey = surveyMapper.getSurveyById(surveyId);
        if (survey == null) {
            return Result.fail("问卷不存在");
        }

        UserInfo userInfo = UserHolder.getCurrentUser();
        if (!survey.getCreatorId().equals(userInfo.getId())) {
            return Result.fail("无权操作该问卷");
        }

        surveyMapper.deleteById(surveyId);
        return Result.success();
    }

    @Override
    public Result<SurveyStatusVO> publishSurvey(Long surveyId) {

        Survey survey = surveyMapper.getSurveyById(surveyId);
        if (survey == null) {
            return Result.fail("问卷不存在");
        }

        if (!SurveyConstant.STATUS_DRAFT.equals(survey.getStatus())) {
            return Result.fail("仅草稿状态的问卷可以发布");
        }

        int questionCount = surveyMapper.countQuestions(surveyId);
        if (questionCount == 0) {
            return Result.fail("发布失败，问卷中至少需要一道题目");
        }

        survey.setStatus(SurveyConstant.STATUS_PUBLISHED);
        surveyMapper.update(survey);

        SurveyStatusVO vo = new SurveyStatusVO(surveyId, survey.getStatus(), survey.getUpdateAt());
        return Result.success(vo);
    }

    @Override
    public Result<SurveyStatusVO> closeSurvey(Long surveyId) {

        Survey survey = surveyMapper.getSurveyById(surveyId);
        if (survey == null) {
            return Result.fail("问卷不存在");
        }

        if (!SurveyConstant.STATUS_PUBLISHED.equals(survey.getStatus())) {
            return Result.fail("仅已发布的问卷可以关闭");
        }

        survey.setStatus(SurveyConstant.STATUS_CLOSED);
        surveyMapper.update(survey);

        SurveyStatusVO vo = new SurveyStatusVO(surveyId, survey.getStatus(), survey.getUpdateAt());
        return Result.success(vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<SurveyCopyVO> copySurvey(Long surveyId) {

        Survey original = surveyMapper.getSurveyById(surveyId);
        if (original == null) {
            return Result.fail("问卷不存在");
        }

        // 1. 创建新问卷（DRAFT）
        Survey newSurvey = new Survey();
        BeanUtil.copyProperties(original, newSurvey);
        newSurvey.setId(null);
        newSurvey.setTitle(original.getTitle() + " - 副本");
        newSurvey.setStatus(SurveyConstant.STATUS_DRAFT);
        newSurvey.setQuestionCount(0);
        newSurvey.setResponseCount(0);

        surveyMapper.insert(newSurvey);
        Long newSurveyId = newSurvey.getId();

        // 2. 复制题目和选项
        List<Question> questionList = questionMapper.getById(surveyId);
        Map<Long, Long> questionIdMap = new HashMap<>();

        for (Question question : questionList) {
            Question newQuestion = new Question();
            BeanUtil.copyProperties(question, newQuestion);
            newQuestion.setId(null);
            newQuestion.setSurveyId(newSurveyId);

            questionMapper.insert(newQuestion);
            Long newQuestionId = newQuestion.getId();
            questionIdMap.put(question.getId(), newQuestionId);

            // 复制选项
            List<QuestionOption> optionList = questionOptionMapper.getById(question.getId());
            for (QuestionOption option : optionList) {
                QuestionOption newOption = new QuestionOption();
                BeanUtil.copyProperties(option, newOption);
                newOption.setId(null);
                newOption.setQuestionId(newQuestionId);
                questionOptionMapper.insert(newOption);
            }
        }

        // 更新新问卷的题目数量
        newSurvey.setQuestionCount(questionList.size());
        surveyMapper.update(newSurvey);

        SurveyCopyVO vo = new SurveyCopyVO(
                newSurveyId, newSurvey.getTitle(), newSurvey.getStatus(),
                newSurvey.getQuestionCount(), newSurvey.getResponseCount(),
                newSurvey.getCreateAt()
        );
        return Result.success(vo);
    }

    @Override
    public Result<SurveyPreviewVO> previewSurvey(Long surveyId) {

        Survey survey = surveyMapper.getSurveyById(surveyId);
        if (survey == null) {
            return Result.fail("问卷不存在");
        }

        SurveyPreviewVO previewVO = new SurveyPreviewVO();
        previewVO.setSurveyId(survey.getId());
        previewVO.setTitle(survey.getTitle());
        previewVO.setDescription(survey.getDescription());
        previewVO.setIsAnonymous(survey.getIsAnonymous());
        previewVO.setStatus(survey.getStatus());

        List<Question> questionList = questionMapper.getById(surveyId);
        List<QuestionVO> questionVOList = new ArrayList<>();

        for (Question question : questionList) {
            QuestionVO questionVO = BeanUtil.copyProperties(question, QuestionVO.class);
            Long qId = question.getId();
            List<QuestionOption> optionList = questionOptionMapper.getById(qId);
            List<QuestionOptionVO> optionVOList = new ArrayList<>();
            for (QuestionOption option : optionList) {
                QuestionOptionVO optionVO = BeanUtil.copyProperties(option, QuestionOptionVO.class);
                optionVOList.add(optionVO);
            }
            questionVO.setOptions(optionVOList);
            questionVOList.add(questionVO);
        }
        previewVO.setQuestions(questionVOList);

        return Result.success(previewVO);
    }

    @Override
    public PageResult<TemplateListVO> listTemplates(Integer page, Integer size) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 10;

        PageHelper.startPage(page, size);
        Page<SurveyTemplate> pageResult = templateMapper.pageQuery(null);

        List<TemplateListVO> records = pageResult.getResult().stream().map(t -> {
            TemplateListVO vo = new TemplateListVO();
            BeanUtil.copyProperties(t, vo);
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(page, size, pageResult.getTotal(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<SurveyCopyVO> applyTemplate(Long templateId, String title, String description) {
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

        List<QuestionDTO> questionDTOs;
        try {
            questionDTOs = objectMapper.readValue(template.getQuestions(),
                    new TypeReference<List<QuestionDTO>>() {});
        } catch (JsonProcessingException e) {
            log.error("解析模板题目数据失败: templateId={}", templateId, e);
            return Result.fail("模板数据解析失败");
        }

        if (questionDTOs != null) {
            for (QuestionDTO qDTO : questionDTOs) {
                Question question = new Question();
                question.setSurveyId(survey.getId());
                question.setType(qDTO.getType());
                question.setTitle(qDTO.getTitle());
                question.setRequired(qDTO.getRequired());
                question.setSortOrder(qDTO.getSortOrder());
                question.setMinRating(qDTO.getMinRating());
                question.setMaxRating(qDTO.getMaxRating());
                question.setCreateAt(LocalDateTime.now());
                question.setUpdateAt(LocalDateTime.now());
                questionMapper.insert(question);

                if (qDTO.getOptions() != null && !qDTO.getOptions().isEmpty()) {
                    List<QuestionOption> optionList = qDTO.getOptions().stream().map(oDTO -> {
                        QuestionOption option = new QuestionOption();
                        option.setQuestionId(question.getId());
                        option.setLabel(oDTO.getLabel());
                        option.setSortOrder(oDTO.getSortOrder());
                        option.setCreateAt(LocalDateTime.now());
                        return option;
                    }).collect(Collectors.toList());
                    questionOptionMapper.insertOptionsBatch(optionList);
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

}
