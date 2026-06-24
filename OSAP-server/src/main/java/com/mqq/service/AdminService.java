package com.mqq.service;

import com.mqq.result.PageResult;
import com.mqq.result.Result;
import com.mqq.vo.AdminUserVO;
import com.mqq.vo.AdminSurveyVO;

public interface AdminService {
    PageResult<AdminUserVO> listUsers(Integer page, Integer size, String keyword, String status);
    Result<Void> updateUserStatus(Long userId, String status);
    PageResult<AdminSurveyVO> listSurveys(Integer page, Integer size, String status);
    Result<Void> forceDeleteSurvey(Long surveyId);
}
