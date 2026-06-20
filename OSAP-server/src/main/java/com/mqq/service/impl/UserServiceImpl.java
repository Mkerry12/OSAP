package com.mqq.service.impl;

import com.mqq.UserHolder.UserHolder;
import com.mqq.constant.RedisConstant;
import com.mqq.constant.SystemConstant;
import com.mqq.dto.UserPasswordUpdateDTO;
import com.mqq.dto.UserPhoneUpdateDTO;
import com.mqq.dto.UserProfileUpdateDTO;
import com.mqq.entity.User;
import com.mqq.mapper.UserMapper;
import com.mqq.result.Result;
import com.mqq.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result<User> profile() {

        User user = userMapper.getById(UserHolder.getCurrentUser().getId());

        return Result.success(user);
    }

    @Override
    public Result updateProfile(UserProfileUpdateDTO userProfileUpdateDTO) {

        Long userId = UserHolder.getCurrentUser().getId();

        userMapper.updateProfile(userProfileUpdateDTO,userId);
        return Result.success();
    }

    @Override
    public Result updatePassword(UserPasswordUpdateDTO userPasswordUpdateDTO) {

        String oldPassword = userPasswordUpdateDTO.getOldPassword();
        String newPassword = userPasswordUpdateDTO.getNewPassword();

        User user = userMapper.getById(UserHolder.getCurrentUser().getId());

        String old_PASSWORD = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        if(!user.getPassword().equals(old_PASSWORD)){
            return Result.fail(SystemConstant.MSG_PASSWORD_OLD_ERROR);
        }
        String phone = user.getPhone();
        userMapper.updatePassword(newPassword,phone);
        return Result.success();
    }

    @Override
    public Result updatePhone(UserPhoneUpdateDTO userPhoneUpdateDTO) {

        Long userId = UserHolder.getCurrentUser().getId();
        User user = userMapper.getById(userId);

        if(!userPhoneUpdateDTO.getOldPhone().equals(user.getPhone())){
            return Result.fail("请输入当前账户的原手机号");
        }

        String RedisCode = stringRedisTemplate.opsForValue().get(RedisConstant.LOGIN_CODE + user.getPhone());

        if (!userPhoneUpdateDTO.getCode().equals(RedisCode)) {
            return Result.fail(SystemConstant.MSG_CODE_ERROR);
        }

        String newPhone = userPhoneUpdateDTO.getNewPhone();

        userMapper.updatePhone(newPhone,userId);
        return Result.success();

    }
}
