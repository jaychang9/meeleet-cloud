package com.meeleet.cloud.common.exception;


import com.meeleet.cloud.common.result.IResultCode;

/**
 * <p>校验异常</p>
 * <p>调用接口时，参数格式不合法可以抛出该异常</p>
 *
 * @author sprainkle
 * @date 2019/5/2
 */
public class ValidationException extends  BaseException {

    private static final long serialVersionUID = 1L;

    public ValidationException(IResultCode responseEnum, Object[] args, String message) {
        super(responseEnum, args, message);
    }

    public ValidationException(IResultCode responseEnum, Object[] args, String message, Throwable cause) {
        super(responseEnum, args, message, cause);
    }
}
