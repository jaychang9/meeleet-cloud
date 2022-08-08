package com.meeleet.cloud.common.exception.assertion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;


class BusinessExceptionAssertTest {


    @SuppressWarnings("ConstantConditions")
    @Test
    void t1() {
        Object user = null;
        AuthResultEnum.USER_MUST_NOT_NULL.assertNotNull(user);
    }

    @Test
    void t2() {
        String code = "6661";
        AuthResultEnum.VERIFY_CODE_NOT_CORRECT.assertIsTrue("6666".equals(code));
    }

    @Getter
    @AllArgsConstructor
    public enum AuthResultEnum implements BusinessExceptionAssert {

        USER_MUST_NOT_NULL("U10110", "用户不存在"),
        VERIFY_CODE_NOT_CORRECT("U10111", "验证码不正确"),
        ;

        private final String code;
        private final String message;
    }
}