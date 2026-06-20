package com.mqq.interceptor;

import cn.hutool.core.bean.BeanUtil;
import com.mqq.UserHolder.UserHolder;
import com.mqq.constant.RedisConstant;
import com.mqq.entity.UserInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class TokenRefreshInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getHeader("Authorization");

        if(token == null){
            return true;
        }

        //使用token从session获取信息
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(RedisConstant.LOGIN_TOKEN+token);
        if(map.isEmpty()){
            return true;
        }

        UserInfo userInfo = BeanUtil.fillBeanWithMap(map,new UserInfo(),false);

        UserHolder.setCurrentUser(userInfo);


        stringRedisTemplate.expire(RedisConstant.LOGIN_TOKEN+token,RedisConstant.LOGIN_TIMEOUT, TimeUnit.MINUTES);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {

        UserHolder.removeCurrentUser();

    }
}
