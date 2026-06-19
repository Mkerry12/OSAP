package com.mqq.result;

import com.mqq.constant.SystemConstant;
import lombok.Data;

import java.io.Serializable;

import static com.mqq.constant.SystemConstant.CODE_FAILURE;
import static com.mqq.constant.SystemConstant.CODE_SUCCESS;

/**
 * 封装后端返回数据结果
 * @param <T>
 */

@Data
public class Result<T> implements Serializable {

    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> success(){
        Result<T> result = new Result<T>();
        result.code = CODE_SUCCESS;
        result.msg = "success";
        return result;
    }
    public static <T> Result<T> success(T data){
        Result<T> result = new Result<T>();
        result.code = CODE_SUCCESS;
        result.msg = "success";
        result.data = data;
        return result;
    }
    public static <T> Result<T> fail(String msg){
        Result<T> result = new Result<T>();
        result.code = CODE_FAILURE;
        result.msg = msg;
        return result;
    }

}
