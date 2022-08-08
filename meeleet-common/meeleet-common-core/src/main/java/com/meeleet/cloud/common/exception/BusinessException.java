package com.meeleet.cloud.common.exception;

import com.meeleet.cloud.common.result.IResultCode;
import lombok.Getter;

@Getter
public class BusinessException extends BaseException {

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
