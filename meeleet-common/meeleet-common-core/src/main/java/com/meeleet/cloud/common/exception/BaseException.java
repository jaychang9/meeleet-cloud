package com.meeleet.cloud.common.exception;


import com.meeleet.cloud.common.result.IResultCode;

/**
 * <p>异常基类</p>
 *
 * @author jaychang
 */
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = -6017282393158435478L;

    /**
     * 返回码
     */
    protected IResultCode resultCode;

    /**
     * 异常消息参数
     */
    protected Object[] args;

    public BaseException(IResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public BaseException(String code, String message) {
        super(message);
        this.resultCode = new IResultCode() {
            @Override
            public String getCode() {
                return code;
            }

            @Override
            public String getMessage() {
                return message;
            }
        };
    }

    public BaseException(IResultCode resultCode, Object[] args, String message) {
        super(message);
        this.resultCode = resultCode;
        this.args = args;
    }

    public BaseException(IResultCode resultCode, Object[] args, String message, Throwable cause) {
        super(message, cause);
        this.resultCode = resultCode;
        this.args = args;
    }

    public IResultCode getResultCode() {
        return resultCode;
    }

    public void setResultCode(IResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
