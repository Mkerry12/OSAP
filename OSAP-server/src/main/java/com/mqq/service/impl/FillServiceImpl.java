package com.mqq.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mqq.UserHolder.UserHolder;
import com.mqq.dto.AnswerSubmitDTO;
import com.mqq.dto.SubmitDTO;
import com.mqq.entity.*;
import com.mqq.mapper.*;
import com.mqq.result.PageResult;
import com.mqq.result.Result;
import com.mqq.service.FillService;
import com.mqq.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class FillServiceImpl implements FillService {

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
    private UserMapper userMapper;

    @Override
    public Result getFill(Long surveyId) {
        Survey survey = surveyMapper.getSurveyById(surveyId);
        if (survey == null) {
            return Result.fail("问卷不存在");
        }
        if (survey.getStatus().equals("CLOSED")) {
            return Result.fail("问卷已经关闭了");
        }
        if (survey.getEndTime() != null && survey.getEndTime().isBefore(LocalDateTime.now())) {
            return Result.fail("问卷已经结束了");
        }
        if (survey.getStartTime() != null && survey.getStartTime().isAfter(LocalDateTime.now())) {
            return Result.fail("问卷尚未开始");
        }

        SurveyFillVO surveyFillVO = BeanUtil.copyProperties(survey, SurveyFillVO.class);
        surveyFillVO.setSurveyId(survey.getId());

        Long userId = UserHolder.getCurrentUser().getId();
        boolean submitted = survey.getAllowMultiSubmit() == 0
                && submissionMapper.countBySurveyAndUser(surveyId, userId) > 0;
        surveyFillVO.setSubmitted(submitted);

        List<Question> questionList = questionMapper.getById(surveyId);
        List<QuestionVO> questionVOList = new ArrayList<>();

        for (Question question : questionList) {
            QuestionVO questionVO = BeanUtil.copyProperties(question, QuestionVO.class);
            List<QuestionOption> questionOptionList = questionOptionMapper.getById(question.getId());

            if (!CollectionUtil.isEmpty(questionOptionList)) {
                List<QuestionOptionVO> questionOptionVOList = new ArrayList<>();
                for (QuestionOption questionOption : questionOptionList) {
                    QuestionOptionVO questionOptionVO = BeanUtil.copyProperties(questionOption, QuestionOptionVO.class);
                    questionOptionVOList.add(questionOptionVO);
                }
                questionVO.setOptions(questionOptionVOList);
            }
            questionVOList.add(questionVO);
        }
        surveyFillVO.setQuestions(questionVOList);

        return Result.success(surveyFillVO);
    }

    @Override
    @Transactional(noRollbackFor = DuplicateKeyException.class)
    public Result<SubmitVO> submitSurvey(Long surveyId, SubmitDTO submitDTO) {
        Survey survey = surveyMapper.getSurveyById(surveyId);
        if (survey == null) {
            return Result.fail("问卷不存在");
        }
        if (!survey.getStatus().equals("PUBLISHED")) {
            return Result.fail("问卷未发布，无法提交");
        }
        if (survey.getEndTime() != null && survey.getEndTime().isBefore(LocalDateTime.now())) {
            return Result.fail("问卷已经结束了");
        }

        Long userId = UserHolder.getCurrentUser().getId();

        String idempotencyKey = submitDTO.getIdempotencyKey();
        boolean hasIdempotencyKey = idempotencyKey != null && !idempotencyKey.isEmpty();

        if (hasIdempotencyKey) {
            Submission existing = submissionMapper.getByIdempotencyKey(idempotencyKey);
            if (existing != null) {
                log.info("幂等命中: key={}, existingSubmissionId={}", idempotencyKey, existing.getId());
                return Result.success(new SubmitVO(existing.getId()));
            }
        }

        if (survey.getAllowMultiSubmit() == 0) {
            int count = submissionMapper.countBySurveyAndUser(surveyId, userId);
            if (count > 0) {
                return Result.fail("您已提交过该问卷，不可重复提交");
            }
        }

        try {
            Submission submission = Submission.builder()
                    .surveyId(surveyId)
                    .userId(userId)
                    .idempotencyKey(idempotencyKey)
                    .duration(submitDTO.getDuration())
                    .submitAt(LocalDateTime.now())
                    .build();
            submissionMapper.insert(submission);

            List<Answer> answers = new ArrayList<>();
            for (AnswerSubmitDTO answerDTO : submitDTO.getAnswers()) {
                answers.add(Answer.builder()
                        .submissionId(submission.getId())
                        .questionId(answerDTO.getQuestionId())
                        .value(answerDTO.getValue())
                        .build());
            }
            if (!answers.isEmpty()) {
                answerMapper.insertBatch(answers);
            }

            surveyMapper.incrementResponseCount(surveyId);

            log.info("问卷提交成功: surveyId={}, userId={}, submissionId={}", surveyId, userId, submission.getId());
            return Result.success(new SubmitVO(submission.getId()));

        } catch (DuplicateKeyException e) {
            log.info("幂等键冲突，查询已有提交: key={}", idempotencyKey);
            Submission existing = submissionMapper.getByIdempotencyKey(idempotencyKey);
            if (existing != null) {
                return Result.success(new SubmitVO(existing.getId()));
            }
            throw new DuplicateKeyException("数据库已经有提交！");
        }
    }

    @Override
    public Result<ResponseDetailVO> getResponseDetail(Long surveyId, Long responseId) {
        // 1. 获取提交记录
        Submission submission = submissionMapper.getByIdAndSurveyId(responseId, surveyId);
        if (submission == null) {
            return Result.fail("答卷不存在");
        }

        // 2. 鉴权：本人/创建者/管理员可查看
        Long currentUserId = UserHolder.getCurrentUser().getId();
        Survey survey = surveyMapper.getSurveyById(surveyId);
        if (!submission.getUserId().equals(currentUserId)
                && !survey.getCreatorId().equals(currentUserId)) {
            User currentUser = userMapper.getById(currentUserId);
            if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
                return Result.fail("无权查看该答卷");
            }
        }

        // 3. 回答者信息
        User respondent = userMapper.getById(submission.getUserId());
        String respondentName = respondent != null ? respondent.getUsername() : "匿名";

        // 4. 获取问卷所有题目 + 选项（用于 label 解析）
        List<Question> questions = questionMapper.getById(surveyId);
        Map<Long, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        List<Long> questionIds = questions.stream().map(Question::getId).toList();
        List<QuestionOption> allOptions = questionOptionMapper.getByQuestionIds(questionIds);
        Map<Long, QuestionOption> optionMap = allOptions.stream()
                .collect(Collectors.toMap(QuestionOption::getId, o -> o));

        // 5. 构建回答明细
        List<Answer> answers = answerMapper.getBySubmissionId(submission.getId());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<AnswerDetailVO> answerDetailVOs = new ArrayList<>();

        for (Answer answer : answers) {
            AnswerDetailVO vo = new AnswerDetailVO();
            vo.setQuestionId(answer.getQuestionId());
            vo.setValue(answer.getValue());

            Question question = questionMap.get(answer.getQuestionId());
            if (question != null) {
                vo.setQuestionTitle(question.getTitle());
                vo.setQuestionType(question.getType());
            }

            // 解析选项 label
            vo.setLabel(resolveLabel(answer.getValue(), question, optionMap));

            answerDetailVOs.add(vo);
        }

        // 6. 组装返回
        ResponseDetailVO responseDetail = new ResponseDetailVO();
        responseDetail.setId(submission.getId());
        responseDetail.setRespondent(respondentName);
        responseDetail.setSubmittedAt(submission.getSubmitAt().format(dtf));
        responseDetail.setDuration(submission.getDuration());
        responseDetail.setAnswers(answerDetailVOs);

        return Result.success(responseDetail);
    }

    @Override
    public PageResult<ResponseListItemVO> getResponseList(Long surveyId, Integer page, Integer size) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 10;

        // 仅创建者或管理员可查看
        Survey survey = surveyMapper.getSurveyById(surveyId);
        if (survey == null) {
            return new PageResult<>(page, size, 0L, List.of());
        }
        Long userId = UserHolder.getCurrentUser().getId();
        if (!survey.getCreatorId().equals(userId)) {
            User user = userMapper.getById(userId);
            if (user == null || !"ADMIN".equals(user.getRole())) {
                return new PageResult<>(page, size, 0L, List.of());
            }
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        boolean isAnonymous = survey.getIsAnonymous() != null && survey.getIsAnonymous() == 1;

        PageHelper.startPage(page, size);
        List<Map<String, Object>> rows = submissionMapper.pageBySurveyId(surveyId);
        Page<Map<String, Object>> pageResult = (Page<Map<String, Object>>) rows;

        List<ResponseListItemVO> records = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            ResponseListItemVO vo = new ResponseListItemVO();
            vo.setId(((Number) row.get("id")).longValue());
            // 匿名问卷不返回 respondent
            if (!isAnonymous) {
                vo.setRespondent((String) row.get("respondent"));
            }
            if (row.get("submit_at") != null) {
                Object sa = row.get("submit_at");
                if (sa instanceof java.sql.Timestamp ts) {
                    vo.setSubmittedAt(ts.toLocalDateTime().format(dtf));
                } else {
                    vo.setSubmittedAt(sa.toString());
                }
            }
            if (row.get("duration") != null) {
                vo.setDuration(((Number) row.get("duration")).intValue());
            }
            records.add(vo);
        }

        return new PageResult<>(page, size, pageResult.getTotal(), records);
    }

    @Override
    public PageResult<MyAssignedSurveyVO> getMyAssigned(Integer page, Integer size) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 10;

        Long userId = UserHolder.getCurrentUser().getId();
        User currentUser = userMapper.getById(userId);
        String phone = currentUser != null ? currentUser.getPhone() : "";

        PageHelper.startPage(page, size);
        Page<Survey> surveyPage = surveyMapper.queryMyAssigned(userId, phone);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Map<Long, User> creatorCache = new HashMap<>();

        List<MyAssignedSurveyVO> records = new ArrayList<>();
        for (Survey survey : surveyPage.getResult()) {
            MyAssignedSurveyVO vo = new MyAssignedSurveyVO();
            vo.setId(survey.getId());
            vo.setTitle(survey.getTitle());
            vo.setDescription(survey.getDescription());
            vo.setType(survey.getType());
            vo.setQuestionCount(survey.getQuestionCount());
            vo.setEndTime(survey.getEndTime() != null ? survey.getEndTime().format(dtf) : null);
            vo.setCreatedAt(survey.getCreateAt() != null ? survey.getCreateAt().format(dtf) : null);

            // 缓存查询创建者
            Long creatorId = survey.getCreatorId();
            if (!creatorCache.containsKey(creatorId)) {
                creatorCache.put(creatorId, userMapper.getById(creatorId));
            }
            vo.setCreator(CreatorVO.from(creatorCache.get(creatorId)));

            records.add(vo);
        }

        return new PageResult<>(page, size, surveyPage.getTotal(), records);
    }

    @Override
    public PageResult<MySubmittedSurveyVO> getMySubmitted(Integer page, Integer size) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 10;

        Long userId = UserHolder.getCurrentUser().getId();

        PageHelper.startPage(page, size);
        Page<Survey> surveyPage = surveyMapper.queryMySubmitted(userId);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 批量查询各问卷的最新提交时间
        List<Long> surveyIds = surveyPage.getResult().stream().map(Survey::getId).toList();
        Map<Long, String> latestSubmitMap = new HashMap<>();
        if (!surveyIds.isEmpty()) {
            List<Map<String, Object>> times = submissionMapper.batchLatestSubmitAt(surveyIds);
            for (Map<String, Object> row : times) {
                Long sid = ((Number) row.get("survey_id")).longValue();
                Object sa = row.get("latest_submit_at");
                if (sa != null) {
                    if (sa instanceof java.sql.Timestamp ts) {
                        latestSubmitMap.put(sid, ts.toLocalDateTime().format(dtf));
                    } else {
                        latestSubmitMap.put(sid, sa.toString());
                    }
                }
            }
        }

        Map<Long, User> creatorCache = new HashMap<>();

        List<MySubmittedSurveyVO> records = new ArrayList<>();
        for (Survey survey : surveyPage.getResult()) {
            MySubmittedSurveyVO vo = new MySubmittedSurveyVO();
            vo.setId(survey.getId());
            vo.setTitle(survey.getTitle());
            vo.setDescription(survey.getDescription());
            vo.setType(survey.getType());
            vo.setStatus(survey.getStatus());
            vo.setQuestionCount(survey.getQuestionCount());
            vo.setAllowMultiSubmit(survey.getAllowMultiSubmit());
            vo.setEndTime(survey.getEndTime() != null ? survey.getEndTime().format(dtf) : null);
            vo.setSubmittedAt(latestSubmitMap.get(survey.getId()));

            // 缓存查询创建者
            Long creatorId = survey.getCreatorId();
            if (!creatorCache.containsKey(creatorId)) {
                creatorCache.put(creatorId, userMapper.getById(creatorId));
            }
            vo.setCreator(CreatorVO.from(creatorCache.get(creatorId)));

            records.add(vo);
        }

        return new PageResult<>(page, size, surveyPage.getTotal(), records);
    }

    /**
     * 根据 value 解析选项文本 label
     */
    private String resolveLabel(String value, Question question, Map<Long, QuestionOption> optionMap) {
        if (value == null || value.isEmpty() || question == null) {
            return null;
        }
        try {
            return switch (question.getType()) {
                case "RADIO", "DROPDOWN" -> {
                    QuestionOption opt = optionMap.get(Long.valueOf(value));
                    yield opt != null ? opt.getLabel() : value;
                }
                case "CHECKBOX" -> Arrays.stream(value.split(","))
                        .map(id -> optionMap.get(Long.valueOf(id.trim())))
                        .filter(Objects::nonNull)
                        .map(QuestionOption::getLabel)
                        .collect(Collectors.joining("; "));
                default -> null; // TEXT / RATING 无 label
            };
        } catch (NumberFormatException e) {
            log.warn("解析选项 label 失败: value={}, type={}", value, question.getType());
            return value;
        }
    }
}
