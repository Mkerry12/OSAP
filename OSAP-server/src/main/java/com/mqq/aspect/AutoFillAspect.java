package com.mqq.aspect;

import com.mqq.UserHolder.UserHolder;
import com.mqq.annotation.AutoFill;
import com.mqq.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    @Pointcut("execution(* com.mqq.mapper.*.*(..)) && @annotation(com.mqq.annotation.AutoFill)")
    public void autoFillPointcut() {}

    @Before("autoFillPointcut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始公共字段自动填充...");

        // 获取方法上的注解，判断操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        // 获取实体参数
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0 || args[0] == null) {
            return;
        }
        Object entity = args[0];

        // 准备填充数据
        LocalDateTime now = LocalDateTime.now();
        com.mqq.entity.UserInfo currentUser = UserHolder.getCurrentUser();

        if (operationType == OperationType.INSERT) {
            try {
                entity.getClass().getDeclaredMethod("setCreateAt", LocalDateTime.class).invoke(entity, now);
            } catch (Exception ignored) {}
            try {
                entity.getClass().getDeclaredMethod("setUpdateAt", LocalDateTime.class).invoke(entity, now);
            } catch (Exception ignored) {}
            if (currentUser != null) {
                try {
                    entity.getClass().getDeclaredMethod("setCreatorId", Long.class).invoke(entity, currentUser.getId());
                } catch (Exception ignored) {}
            }
        } else if (operationType == OperationType.UPDATE) {
            try {
                entity.getClass().getDeclaredMethod("setUpdateAt", LocalDateTime.class).invoke(entity, now);
            } catch (Exception ignored) {}
        }
    }
}
