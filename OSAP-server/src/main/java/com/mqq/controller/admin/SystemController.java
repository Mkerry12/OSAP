package com.mqq.controller.admin;

import com.mqq.result.PageResult;
import com.mqq.result.Result;
import com.mqq.service.SystemService;
import com.mqq.vo.BackupVO;
import com.mqq.vo.OperationLogVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController("adminSystemController")
public class SystemController {

    @Autowired
    private SystemService systemService;

    @GetMapping("/admin/logs")
    public Result<PageResult<OperationLogVO>> listLogs(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "keyword", required = false) String keyword) {
        log.info("查询操作日志: page={}, size={}, type={}", page, size, type);
        PageResult<OperationLogVO> pageResult = systemService.listLogs(page, size, type, startTime, endTime, keyword);
        return Result.success(pageResult);
    }

    @PostMapping("/admin/backup")
    public Result<BackupVO> createBackup() {
        log.info("创建数据备份");
        return systemService.createBackup();
    }

    @GetMapping("/admin/backups")
    public Result<PageResult<BackupVO>> listBackups(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("查询备份列表: page={}, size={}", page, size);
        PageResult<BackupVO> pageResult = systemService.listBackups(page, size);
        return Result.success(pageResult);
    }

    @GetMapping("/admin/backups/{backupId}/download")
    public void downloadBackup(@PathVariable Long backupId,
                                HttpServletResponse response) throws IOException {
        log.info("下载备份文件: backupId={}", backupId);

        response.setContentType("application/octet-stream");
        String filename = URLEncoder.encode("backup_" + backupId, StandardCharsets.UTF_8)
                .replace("+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + filename + ".json");

        try {
            systemService.downloadBackup(backupId, response.getOutputStream());
        } catch (RuntimeException  e) {
            response.reset();
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"code\":0,\"msg\":\"" + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/admin/backups/{backupId}")
    public Result<Void> deleteBackup(@PathVariable Long backupId) {
        log.info("删除备份: backupId={}", backupId);
        return systemService.deleteBackup(backupId);
    }
}
