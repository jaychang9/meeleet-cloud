package com.meeleet.cloud.common.auth.enums;

import com.meeleet.cloud.common.exception.assertion.CommonExceptionAssert;
import com.meeleet.cloud.common.result.IResultCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum AuthResultEnum implements CommonExceptionAssert, IResultCode, Serializable {

    VERIFICATION_CODE_INCORRECT("", "验证码不正确"),
    ;
    private final String code;
    private final String message;
}
