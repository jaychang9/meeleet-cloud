package com.meeleet.cloud.common.exception;

import com.meeleet.cloud.common.result.IResultCode;
import lombok.Getter;

/**
 * 业务异常类
 * @author jaychang
 */
@Getter
public class BusinessException extends BaseException {

    private static final long serialVersionUID = -5495698081831953712L;

    public BusinessException(IResultCode resultCode) {
        super(resultCode);
    }

    public BusinessException(String code, String message) {
        super(code, message);
    }

    public BusinessException(IResultCode resultCode, Object[] args, String message) {
        super(resultCode, args, message);
    }

    public BusinessException(IResultCode resultCode, Object[] args, String message, Throwable cause) {
        super(resultCode, args, message, cause);
    }
}
