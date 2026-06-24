package com.mqq.service;

import com.mqq.dto.UserPasswordLoginDTO;
import com.mqq.dto.UserRegisterDTO;
import com.mqq.result.Result;
import jakarta.servlet.http.HttpSession;

public interface AuthService {
Result sendCode(String phone, HttpSession session);

    Result register(UserRegisterDTO userRegisterDTO, HttpSession session);

    Result loginWithPassword(UserPasswordLoginDTO userPasswordLoginDTO, HttpSession session);

    Result logout(String token);

    Result forgetPassword(String phone, String username, HttpSession session);

    Result forgetPasswordCheck(String code, String phone, HttpSession session);

    Result resetPassword(String phone, String newPassword, HttpSession session);
}
