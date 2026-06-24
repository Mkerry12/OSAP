package com.mqq.controller.admin;

import com.mqq.dto.UserStatusDTO;
import com.mqq.result.PageResult;
import com.mqq.result.Result;
import com.mqq.service.AdminService;
import com.mqq.vo.AdminSurveyVO;
import com.mqq.vo.AdminUserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("adminSurveyManagementController")
public class AdminSurveyController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/admin/users")
    public Result<PageResult<AdminUserVO>> listUsers(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status) {
        log.info("管理员查询用户列表: page={}, size={}, keyword={}, status={}", page, size, keyword, status);
        PageResult<AdminUserVO> pageResult = adminService.listUsers(page, size, keyword, status);
        return Result.success(pageResult);
    }

    @PutMapping("/admin/users/{userId}/status")
    public Result<Void> updateUserStatus(@PathVariable Long userId,
                                          @RequestBody UserStatusDTO userStatusDTO) {
        log.info("管理员修改用户状态: userId={}, status={}", userId, userStatusDTO.getStatus());
        return adminService.updateUserStatus(userId, userStatusDTO.getStatus());
    }

    @GetMapping("/admin/surveys")
    public Result<PageResult<AdminSurveyVO>> listSurveys(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "keyword", required = false) String keyword)
    {
        log.info("管理员查询问卷列表: page={}, size={}, status={}", page, size, status);
        PageResult<AdminSurveyVO> pageResult = adminService.listSurveys(page, size, status,keyword);
        return Result.success(pageResult);
    }

    @DeleteMapping("/admin/surveys/{surveyId}")
    public Result<Void> forceDeleteSurvey(@PathVariable Long surveyId) {
        log.info("管理员强制删除问卷: surveyId={}", surveyId);
        return adminService.forceDeleteSurvey(surveyId);
    }
}
