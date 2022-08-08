package com.meeleet.cloud.common.exception.assertion;


import com.meeleet.cloud.common.exception.BaseException;
import com.meeleet.cloud.common.exception.BusinessException;
import com.meeleet.cloud.common.result.IResultCode;

import java.text.MessageFormat;

/**
 * <p>业务异常断言</p>
 *
 * @author jaychang
 */
public interface BusinessExceptionAssert extends IResultCode, Assert {

    /***
     *<p>创建异常</p>
     *
     * @param args
     * @return com.meeleet.common.exception.BaseException
     */
    @Override
    default BaseException newException(Object... args) {
        String msg = MessageFormat.format(this.getMessage(), args);

        return new BusinessException(this, args, msg);
    }

    /***
     *<p>创建异常</p>
     *
     * @param cause
     * @param args
     * @return com.meeleet.common.exception.BaseException
     */
    @Override
    default BaseException newException(Throwable cause, Object... args) {
        String msg = MessageFormat.format(this.getMessage(), args);

        return new BusinessException(this, args, msg, cause);
    }

}
