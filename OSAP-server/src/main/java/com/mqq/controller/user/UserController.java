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
@RestController
@RequestMapping("/user/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/profile")
    public Result profile(){
        return userService.profile();
    }

    @PutMapping("/profile")
    public Result updateProfile(@RequestBody UserProfileUpdateDTO  userProfileUpdateDTO){
        return userService.updateProfile(userProfileUpdateDTO);
    }

    @PostMapping("/update-password")
    public Result updatePassword(@RequestBody UserPasswordUpdateDTO userPasswordUpdateDTO){
        return userService.updatePassword(userPasswordUpdateDTO);
    }

    @PutMapping("Update-phone")
    public Result updatePhone(@RequestBody UserPhoneUpdateDTO  userPhoneUpdateDTO){
        return userService.updatePhone(userPhoneUpdateDTO);
    }

}
