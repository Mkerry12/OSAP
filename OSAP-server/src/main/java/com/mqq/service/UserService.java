package com.mqq.service;

import com.mqq.dto.UserPasswordLoginDTO;
import com.mqq.dto.UserRegisterDTO;
import com.mqq.result.Result;
import jakarta.servlet.http.HttpSession;

public interface UserService {
    Result login();

    Result sendCode(String phone, HttpSession session);

    Result register(UserRegisterDTO userRegisterDTO, HttpSession session);

    Result loginWithPassword(UserPasswordLoginDTO userPasswordLoginDTO, HttpSession session);
}
