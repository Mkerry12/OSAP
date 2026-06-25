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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import static com.mqq.constant.SystemConstant.MSG_PHONE_NO_MATCH;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
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
        String new_PASSWORD = DigestUtils.md5DigestAsHex(newPassword.getBytes());
        userMapper.updatePassword(new_PASSWORD,phone);
        return Result.success();
    }

    @Override
    public Result updatePhone(UserPhoneUpdateDTO userPhoneUpdateDTO) {

        Long userId = UserHolder.getCurrentUser().getId();
        User user = userMapper.getById(userId);

        if(!userPhoneUpdateDTO.getOldPhone().equals(user.getPhone())){
            return Result.fail(MSG_PHONE_NO_MATCH);
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
