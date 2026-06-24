package com.mqq.controller.user;

import com.mqq.dto.UserPasswordUpdateDTO;
import com.mqq.dto.UserPhoneUpdateDTO;
import com.mqq.dto.UserProfileUpdateDTO;
import com.mqq.result.Result;
import com.mqq.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("userUserController")
@RequestMapping("/user/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/profile")
    public Result profile(){
        log.info("查询用户信息");
        return userService.profile();
    }

    @PutMapping("/profile")
    public Result updateProfile(@RequestBody UserProfileUpdateDTO  userProfileUpdateDTO){
        log.info("更新用户资料");
        return userService.updateProfile(userProfileUpdateDTO);
    }

    @PostMapping("/update-password")
    public Result updatePassword(@RequestBody UserPasswordUpdateDTO userPasswordUpdateDTO){
        log.info("修改密码");
        return userService.updatePassword(userPasswordUpdateDTO);
    }

    @PutMapping("/update-phone")
    public Result updatePhone(@RequestBody UserPhoneUpdateDTO  userPhoneUpdateDTO){
        log.info("更新手机号");
        return userService.updatePhone(userPhoneUpdateDTO);
    }

}
