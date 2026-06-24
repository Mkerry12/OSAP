package com.mqq.controller.common;

import com.mqq.dto.UserPasswordLoginDTO;
import com.mqq.dto.UserRegisterDTO;
import com.mqq.result.Result;
import com.mqq.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/sendcode")
    public Result sendCode(@RequestParam("phone") String phone, HttpSession session) {
        log.info("发送验证码...{}",phone);
        return authService.sendCode(phone, session);
    }

    @PostMapping("/register")
    public Result register(@RequestBody UserRegisterDTO userRegisterDTO, HttpSession session) {
        log.info("注册用户");
        return authService.register(userRegisterDTO, session);
    }

    @PostMapping("/password-login")
    public Result login(@RequestBody UserPasswordLoginDTO userPasswordLoginDTO, HttpSession session) {
        log.info("账号密码登录");
        return authService.loginWithPassword(userPasswordLoginDTO, session);
    }

    @PostMapping("/logout")
    public Result logout(HttpServletRequest request) {
        log.info("退出登录");
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return authService.logout(token);
    }

    @PostMapping("/forget-password/checkInfo")
    public Result forgetPassword(@RequestParam("phone") String phone,
                                 @RequestParam("username") String username,
                                 HttpSession session) {
        log.info("改密码时先对信息进行验证");
        return authService.forgetPassword(phone, username, session);
    }

    @PostMapping("/forget-password/checkcode")
    public Result forgetPasswordCheck(@RequestParam("phone") String phone,
                                      @RequestParam("code") String code,
                                      HttpSession session) {
        log.info("改密码时对验证码进行验证");
        return authService.forgetPasswordCheck(code, phone, session);
    }

    @PutMapping("/reset-password")
    public Result resetPassword(@RequestParam("phone") String phone,
                                @RequestParam("new_password") String new_password,
                                HttpSession session) {
        return authService.resetPassword(phone, new_password, session);
    }
}
