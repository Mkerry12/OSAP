package com.mqq.hander;

import com.mqq.constant.SystemConstant;
import com.mqq.exception.BaseException;
import com.mqq.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex) {
        log.error("异常信息：{}", ex.getMessage());
        return Result.fail(ex.getMessage());
    }

    @ExceptionHandler
    public Result exceptionHandler(DuplicateKeyException ex) {
        log.error("数据库异常信息：{}", ex.getMessage());
        if(ex.getMessage().contains("Duplicate entry"))
        {
            String[] parts = ex.getMessage().split("'");
            if (parts.length >= 2) {
                String value = parts[1];
                return Result.fail(SystemConstant.ALREADY_EXISTS + value);
            }
        }
        return Result.fail("数据库异常");
    }

}