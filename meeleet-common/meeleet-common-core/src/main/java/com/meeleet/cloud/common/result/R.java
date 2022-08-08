package com.meeleet.cloud.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结构体
 *
 * @author jaychang
 **/
@Data
public class R<T> implements Serializable {

    private String code;

    private T data;

    private String msg;

    public static <T> R<T> success() {
        return success(null);
    }

    public static <T> R<T> success(T data) {
        R<T> result = new R<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMsg(ResultCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    public static <T> R<T> failed() {
        return result(ResultCode.SYSTEM_EXECUTION_ERROR.getCode(), ResultCode.SYSTEM_EXECUTION_ERROR.getMessage(), null);
    }

    public static <T> R<T> failed(String msg) {
        return result(ResultCode.SYSTEM_EXECUTION_ERROR.getCode(), msg, null);
    }

    public static <T> R<T> judge(boolean status) {
        if (status) {
            return success();
        } else {
            return failed();
        }
    }

    public static <T> R<T> failed(IResultCode resultCode) {
        return result(resultCode.getCode(), resultCode.getMessage(), null);
    }

    public static <T> R<T> failed(IResultCode resultCode, String msg) {
        return result(resultCode.getCode(), msg, null);
    }

    private static <T> R<T> result(IResultCode resultCode, T data) {
        return result(resultCode.getCode(), resultCode.getMessage(), data);
    }

    private static <T> R<T> result(String code, String msg, T data) {
        R<T> result = new R<>();
        result.setCode(code);
        result.setData(data);
        result.setMsg(msg);
        return result;
    }

    public static boolean isSuccess(R<?> result) {
        return result != null && ResultCode.SUCCESS.getCode().equals(result.getCode());
    }
}
