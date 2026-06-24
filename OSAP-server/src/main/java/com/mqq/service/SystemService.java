package com.mqq.service;

import com.mqq.result.PageResult;
import com.mqq.result.Result;
import com.mqq.vo.BackupVO;
import com.mqq.vo.OperationLogVO;

import java.io.OutputStream;

public interface SystemService {
    PageResult<OperationLogVO> listLogs(Integer page, Integer size, String type,
                                         String startTime, String endTime, String keyword);
    Result<BackupVO> createBackup();
    PageResult<BackupVO> listBackups(Integer page, Integer size);
    void downloadBackup(Long backupId, OutputStream outputStream);
    Result<Void> deleteBackup(Long backupId);
}
