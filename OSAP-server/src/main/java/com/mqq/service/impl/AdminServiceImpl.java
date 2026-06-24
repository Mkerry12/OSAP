package com.mqq.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mqq.UserHolder.UserHolder;
import com.mqq.dto.QuestionDTO;
import com.mqq.dto.QuestionOptionDTO;
import com.mqq.entity.*;
import com.mqq.mapper.*;
import com.mqq.result.PageResult;
import com.mqq.result.Result;
import com.mqq.service.AdminService;
import com.mqq.vo.AdminSurveyVO;
import com.mqq.vo.AdminUserVO;
import com.mqq.vo.CreatorVO;
import com.mqq.vo.TemplateListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SurveyMapper surveyMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionOptionMapper questionOptionMapper;

    @Autowired
    private SubmissionMapper submissionMapper;

    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private TemplateMapper templateMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private void checkAdmin() {
        UserInfo currentUser = UserHolder.getCurrentUser();
        User user = userMapper.getById(currentUser.getId());
        if (user == null || !"ADMIN".equals(user.getRole())) {
            throw new com.mqq.exception.BaseException("无权限操作");
        }
    }

    @Override
    public PageResult<AdminUserVO> listUsers(Integer page, Integer size, String keyword, String status) {
        checkAdmin();
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 10;

        PageHelper.startPage(page, size);
        Page<User> userPage = userMapper.pageQuery(keyword, status);

        List<AdminUserVO> records = new ArrayList<>();
        for (User user : userPage.getResult()) {
            AdminUserVO vo = new AdminUserVO();
            BeanUtil.copyProperties(user, vo);
            records.add(vo);
        }

        return new PageResult<>(page, size, userPage.getTotal(), records);
    }

    @Override
    public Result<Void> updateUserStatus(Long userId, String status) {
        checkAdmin();
        User user = userMapper.getById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }

        Integer statusCode;
        if ("ACTIVE".equalsIgnoreCase(status)) {
            statusCode = 1;
        } else if ("DISABLED".equalsIgnoreCase(status)) {
            statusCode = 0;
        } else {
            return Result.fail("无效的状态值，仅支持 ACTIVE 或 DISABLED");
        }

        UserInfo currentUser = UserHolder.getCurrentUser();
        if (currentUser != null && currentUser.getId().equals(userId)) {
            return Result.fail("不能修改自己的状态");
        }

        userMapper.updateStatus(userId, statusCode);
        log.info("管理员 {} 将用户 {} 状态修改为 {}", currentUser.getUsername(), userId, status);
        return Result.success();
    }

    @Override
    public PageResult<AdminSurveyVO> listSurveys(Integer page, Integer size, String status, String keyword) {
        checkAdmin();
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 10;

        PageHelper.startPage(page, size);
        Page<Survey> surveyPage = surveyMapper.pageQuery(
                new com.mqq.dto.PageQuerySurveyDTO(page, size, status, keyword, "create_at", "DESC"));

        List<AdminSurveyVO> records = new ArrayList<>();
        for (Survey survey : surveyPage.getResult()) {
            AdminSurveyVO vo = new AdminSurveyVO();
            BeanUtil.copyProperties(survey, vo);

            User creator = userMapper.getById(survey.getCreatorId());
            if (creator != null) {
                vo.setCreator(CreatorVO.from(creator));
            }
            records.add(vo);
        }

        return new PageResult<>(page, size, surveyPage.getTotal(), records);
    }

    @Override
    public Result<Void> forceDeleteSurvey(Long surveyId) {
        checkAdmin();
        Survey survey = surveyMapper.getSurveyById(surveyId);
        if (survey == null) {
            return Result.fail("问卷不存在");
        }

        UserInfo currentUser = UserHolder.getCurrentUser();
        log.info("管理员 {} 强制删除问卷 surveyId={}", currentUser.getUsername(), surveyId);

        List<Submission> submissions = submissionMapper.listBySurveyId(surveyId);
        for (Submission sub : submissions) {
            answerMapper.deleteBySubmissionId(sub.getId());
        }
        submissionMapper.deleteBySurveyId(surveyId);

        List<Long> questionIds = questionMapper.getById(surveyId).stream()
                .map(com.mqq.entity.Question::getId)
                .toList();
        for (Long questionId : questionIds) {
            questionOptionMapper.deleteByQuestionId(questionId);
        }
        questionMapper.deleteBySurveyId(surveyId);

        surveyMapper.deleteById(surveyId);

        return Result.success();
    }

    @Override
    public Result<TemplateListVO> convertToTemplate(Long surveyId) {
        checkAdmin();
        Survey survey = surveyMapper.getSurveyById(surveyId);
        if (survey == null) {
            return Result.fail("问卷不存在");
        }

        List<Question> questions = questionMapper.getById(surveyId);
        List<QuestionDTO> questionDTOs = questions.stream().map(q -> {
            QuestionDTO dto = new QuestionDTO();
            dto.setType(q.getType());
            dto.setTitle(q.getTitle());
            dto.setRequired(q.getRequired());
            dto.setSortOrder(q.getSortOrder());
            dto.setMinRating(q.getMinRating());
            dto.setMaxRating(q.getMaxRating());

            List<QuestionOption> options = questionOptionMapper.getById(q.getId());
            if (options != null && !options.isEmpty()) {
                List<QuestionOptionDTO> optionDTOs = options.stream().map(o -> {
                    QuestionOptionDTO oDTO = new QuestionOptionDTO();
                    oDTO.setLabel(o.getLabel());
                    oDTO.setSortOrder(o.getSortOrder());
                    return oDTO;
                }).collect(Collectors.toList());
                dto.setOptions(optionDTOs);
            }
            return dto;
        }).collect(Collectors.toList());

        String questionsJson;
        try {
            questionsJson = objectMapper.writeValueAsString(questionDTOs);
        } catch (JsonProcessingException e) {
            log.error("序列化题目数据失败", e);
            return Result.fail("序列化题目数据失败");
        }

        UserInfo userInfo = UserHolder.getCurrentUser();
        SurveyTemplate template = SurveyTemplate.builder()
                .title(survey.getTitle())
                .description(survey.getDescription())
                .category(null)
                .questions(questionsJson)
                .questionCount(questionDTOs.size())
                .useCount(0)
                .creatorId(userInfo.getId())
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        templateMapper.insert(template);

        TemplateListVO vo = new TemplateListVO();
        vo.setId(template.getId());
        vo.setTitle(template.getTitle());
        vo.setCategory(template.getCategory());
        vo.setQuestionCount(template.getQuestionCount());
        vo.setUseCount(template.getUseCount());
        vo.setCreateAt(template.getCreateAt());

        log.info("管理员 {} 将问卷 {} 转换为模板 {}", userInfo.getUsername(), surveyId, template.getId());
        return Result.success(vo);
    }
}
