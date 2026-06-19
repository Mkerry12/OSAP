package com.mqq.controller.user;

import com.mqq.dto.UserPasswordLoginDTO;
import com.mqq.dto.UserRegisterDTO;
import com.mqq.result.Result;
import com.mqq.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sendcode")
    public Result sendCode(@RequestParam("phone") String phone, HttpSession session) {
        log.info("发送验证码...");
        return userService.sendCode(phone, session);
    }

    @PostMapping("/register")
    public Result register(@RequestBody UserRegisterDTO userRegisterDTO, HttpSession session) {
        log.info("注册用户");
        return userService.register(userRegisterDTO,session);
    }

    @PostMapping("/passwordLogin")
    public Result login(@RequestBody UserPasswordLoginDTO  userPasswordLoginDTO, HttpSession session) {
        log.info("账号密码登录");
        return userService.loginWithPassword(userPasswordLoginDTO,session);
    }
}
