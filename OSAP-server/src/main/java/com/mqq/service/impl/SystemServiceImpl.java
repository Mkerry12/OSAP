package com.mqq.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mqq.UserHolder.UserHolder;
import com.mqq.entity.*;
import com.mqq.mapper.*;
import com.mqq.result.PageResult;
import com.mqq.result.Result;
import com.mqq.service.SystemService;
import com.mqq.vo.BackupVO;
import com.mqq.vo.OperationLogVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class SystemServiceImpl implements SystemService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Autowired
    private BackupMapper backupMapper;

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

    private final Path backupDir = Paths.get(System.getProperty("user.dir"), "backups");

    @Override
    public PageResult<OperationLogVO> listLogs(Integer page, Integer size, String type,
                                                String startTime, String endTime, String keyword) {
        checkAdmin();
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 20;

        PageHelper.startPage(page, size);
        Page<OperationLog> logPage = operationLogMapper.pageQuery(type, startTime, endTime, keyword);

        List<OperationLogVO> records = logPage.getResult().stream().map(l -> {
            OperationLogVO vo = new OperationLogVO();
            BeanUtil.copyProperties(l, vo);
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(page, size, logPage.getTotal(), records);
    }

    @Override
    public Result<BackupVO> createBackup() {
        checkAdmin();
        try {
            Files.createDirectories(backupDir);
        } catch (IOException e) {
            log.error("创建备份目录失败", e);
            return Result.fail("备份目录创建失败");
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "backup_" + timestamp + ".json";

        Map<String, Object> backupData = new LinkedHashMap<>();
        backupData.put("backupTime", LocalDateTime.now().toString());

        List<Survey> surveys = surveyMapper.pageQuery(
                new com.mqq.dto.PageQuerySurveyDTO(1, Integer.MAX_VALUE, null, null, null, null));
        List<Map<String, Object>> surveyData = new ArrayList<>();

        for (Survey survey : surveys) {
            Map<String, Object> sMap = new LinkedHashMap<>();
            sMap.put("survey", survey);
            List<Question> questions = questionMapper.getById(survey.getId());
            sMap.put("questions", questions);
            sMap.put("options", questionOptionMapper.getByQuestionIds(
                    questions.stream().map(Question::getId).toList()));
            surveyData.add(sMap);
        }
        backupData.put("surveys", surveyData);

        Path filePath = backupDir.resolve(fileName);
        try (FileWriter writer = new FileWriter(filePath.toFile(), StandardCharsets.UTF_8)) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, backupData);
        } catch (IOException e) {
            log.error("写入备份文件失败", e);
            return Result.fail("备份文件写入失败");
        }

        long fileSize;
        try {
            fileSize = Files.size(filePath);
        } catch (IOException e) {
            fileSize = 0;
        }

        UserInfo userInfo = UserHolder.getCurrentUser();
        BackupRecord record = BackupRecord.builder()
                .fileName(fileName)
                .fileSize(fileSize)
                .creatorId(userInfo.getId())
                .createAt(LocalDateTime.now())
                .build();
        backupMapper.insert(record);

        BackupVO vo = new BackupVO(record.getId(), record.getFileName(),
                record.getFileSize(), record.getCreateAt());
        return Result.success(vo);
    }

    @Override
    public PageResult<BackupVO> listBackups(Integer page, Integer size) {
        checkAdmin();
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 10;

        PageHelper.startPage(page, size);
        Page<BackupRecord> backupPage = backupMapper.pageQuery();

        List<BackupVO> records = backupPage.getResult().stream().map(b -> {
            BackupVO vo = new BackupVO();
            BeanUtil.copyProperties(b, vo);
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(page, size, backupPage.getTotal(), records);
    }

    @Override
    public void downloadBackup(Long backupId, OutputStream outputStream) {
        checkAdmin();
        BackupRecord record = backupMapper.getById(backupId);
        if (record == null) {
            throw new IllegalArgumentException("备份记录不存在");
        }

        Path filePath = backupDir.resolve(record.getFileName());
        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("备份文件不存在");
        }

        try (InputStream is = Files.newInputStream(filePath)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        } catch (IOException e) {
            log.error("下载备份文件失败", e);
            throw new RuntimeException("下载备份文件失败", e);
        }
    }

    @Override
    public Result<Void> deleteBackup(Long backupId) {
        checkAdmin();
        BackupRecord record = backupMapper.getById(backupId);
        if (record == null) {
            return Result.fail("备份记录不存在");
        }

        Path filePath = backupDir.resolve(record.getFileName());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("删除备份文件失败: {}", filePath, e);
        }

        backupMapper.deleteById(backupId);
        return Result.success();
    }
}
