package com.mqq.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.mqq.constant.RedisConstant;
import com.mqq.constant.SystemConstant;
import com.mqq.dto.UserPasswordLoginDTO;
import com.mqq.dto.UserRegisterDTO;
import com.mqq.entity.User;
import com.mqq.mapper.UserMapper;
import com.mqq.result.Result;
import com.mqq.service.AuthService;
import com.mqq.utils.RegexUtils;
import com.mqq.vo.UserPasswordLoginVO;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.mqq.constant.RedisConstant.*;
import static com.mqq.constant.SystemConstant.*;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    private final StringRedisTemplate stringRedisTemplate;

    public AuthServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Override
    public Result login() {
        return null;
    }

    @Override
    public Result sendCode(String phone, HttpSession session) {

        if (RegexUtils.isPhoneInvalid(phone)) {
            return Result.fail(MSG_PHONE_INVALID);
        }

        String RandomNumber = RandomUtil.randomNumbers(6);

        stringRedisTemplate.opsForValue().set(LOGIN_CODE + phone, RandomNumber, CODE_TIMEOUT, TimeUnit.MINUTES);
        log.info("发送短信验证码成功,五分钟内有效:{}", RandomNumber);

        return Result.success();

    }

    @Override
    public Result register(UserRegisterDTO userRegisterDTO, HttpSession session) {

        if (RegexUtils.isPhoneInvalid(userRegisterDTO.getPhone())) {
            return Result.fail(MSG_PHONE_INVALID);
        }
        if (RegexUtils.isEmailInvalid(userRegisterDTO.getEmail())) {
            return Result.fail(MSG_EMAIL_INVALID);
        }

        String RedisCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE + userRegisterDTO.getPhone());

        String codeTakeIn = userRegisterDTO.getCode();

        if(codeTakeIn == null || !codeTakeIn.equals(RedisCode)) {
            return Result.fail(SystemConstant.MSG_CODE_ERROR);
        }

        String password = userRegisterDTO.getPassword();
        String PASSWORD = DigestUtils.md5DigestAsHex(password.getBytes());


        User user = User.builder()
                .role("USER")
                .image("DefaultImage")
                .status(0)
                .username(userRegisterDTO.getUsername())
                .password(PASSWORD)
                .email(userRegisterDTO.getEmail())
                .phone(userRegisterDTO.getPhone())

                .build();

        userMapper.insert(user);

        return Result.success();

    }

    @Override
    public Result loginWithPassword(UserPasswordLoginDTO userPasswordLoginDTO, HttpSession session) {

        String Username = userPasswordLoginDTO.getUsername();
        String Password = userPasswordLoginDTO.getPassword();

        User user = userMapper.getByUsername(Username);

        if (user == null) {
            return Result.fail(MSG_USER_NOT_EXIST);
        }

        String PASSWORD = user.getPassword();
        if (!Password.equals(PASSWORD)) {
            return Result.fail(MSG_CHECK_ACC_OR_PAS);
        }

        String token = UUID.randomUUID().toString(true);

        HashMap<String, Object> map = new HashMap<>();
        map.put("id",user.getId());
        map.put("username", user.getUsername());
        map.put("phone", user.getPhone());
        map.put("email", user.getEmail());

        stringRedisTemplate.opsForHash().putAll(LOGIN_TOKEN + token,map);
        stringRedisTemplate.expire(LOGIN_TOKEN,LOGIN_TIMEOUT,TimeUnit.MINUTES);

        UserPasswordLoginVO userPasswordLoginVO = new UserPasswordLoginVO();
        userPasswordLoginVO.setToken(token);
        userPasswordLoginVO.setExpiresIn(String.valueOf(RedisConstant.LOGIN_TIMEOUT));
        User user_vo = User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .image(user.getImage())
                .build();
        userPasswordLoginVO.setUser(user_vo);
        return Result.success(userPasswordLoginVO);
    }

    @Override
    public Result logout(String token) {
        stringRedisTemplate.delete(LOGIN_TOKEN + token);
        return Result.success(MSG_LOGOUT_SUCCESS);
    }

    @Override
    public Result forgetPassword(String phone, String username, HttpSession session) {

        User user = userMapper.getByUsername(username);

        if(user == null) {
            return Result.fail(MSG_USER_NOT_EXIST);
        }
        if(!user.getPhone().equals(phone)) {
            return Result.fail(MSG_PHONE_INVALID);
        }

        log.info("已经向{}发送验证码", phone);

        return sendCode(phone,session);

    }

    @Override
    public Result forgetPasswordCheck(String code, String phone, HttpSession session) {
        String Rediscode = stringRedisTemplate.opsForValue().get(LOGIN_CODE + phone);
        if(Rediscode == null || !Rediscode.equals(code)) {
            return Result.fail(SystemConstant.MSG_CODE_ERROR);
        }
        return Result.success("验证成功，请重置密码");
    }

    @Override
    public Result resetPassword(String phone, String newPassword, HttpSession session) {

        userMapper.updatePassword(phone, newPassword);

        return Result.success();
    }


}
