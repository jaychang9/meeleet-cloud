package com.meeleet.cloud.common.web.exception;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.meeleet.cloud.common.constant.GlobalConstants;
import com.meeleet.cloud.common.exception.BaseException;
import com.meeleet.cloud.common.exception.BusinessException;
import com.meeleet.cloud.common.i18n.UnifiedMessageSource;
import com.meeleet.cloud.common.result.IResultCode;
import com.meeleet.cloud.common.result.R;
import com.meeleet.cloud.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.ServletException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.util.concurrent.CompletionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 全局系统异常处理
 * 调整异常处理的HTTP状态码，丰富异常处理类型
 *
 * @author jaychang
 * @date 2020-02-25 13:54
 * <p>
 **/
@Import(UnifiedMessageSource.class)
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final UnifiedMessageSource unifiedMessageSource;

    /**
     * 当前环境
     */
    private final String profile;

    public GlobalExceptionHandler(UnifiedMessageSource unifiedMessageSource, @Value("${spring.profiles.active}") String profile) {
        this.unifiedMessageSource = unifiedMessageSource;
        this.profile = profile;
    }

    /**
     * 获取国际化消息
     *
     * @param e 异常
     * @return
     */
    public String getMessage(BaseException e) {
        String code = "response." + e.getResultCode().toString();
        String message = unifiedMessageSource.getMessage(code, e.getArgs());
        if (StrUtil.isBlank(message)) {
            return e.getMessage();
        }
        return message;
    }

    /**
     * 获取国际化消息
     *
     * @param e 异常
     * @return
     */
    public String getMessage(IResultCode resultCode) {
        String code = "response." + resultCode.toString();
        String message = unifiedMessageSource.getMessage(code);
        if (StrUtil.isBlank(message)) {
            return resultCode.getMessage();
        }
        return message;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public <T> R<T> handleException(NoHandlerFoundException e) {
        log.error("未找到匹配的请求处理器", e);
        if (GlobalConstants.ENV_PROD.equals(profile)) {
            String message = getMessage(ResultCode.SYSTEM_EXECUTION_ERROR);
            return R.failed(message);
        }
        return R.failed(ResultCode.RESOURCE_NOT_FOUND, getMessage(ResultCode.RESOURCE_NOT_FOUND));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public <T> R<T> handleException(HttpRequestMethodNotSupportedException e) {
        log.error("请求方法不支持", e);
        if (GlobalConstants.ENV_PROD.equals(profile)) {
            String message = getMessage(ResultCode.SYSTEM_EXECUTION_ERROR);
            return R.failed(message);
        }
        return R.failed(ResultCode.REQUEST_METHOD_NOT_SUPPORTED, getMessage(ResultCode.REQUEST_METHOD_NOT_SUPPORTED));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public <T> R<T> handleException(HttpMediaTypeNotSupportedException e) {
        log.error("请求头content-type不支持", e);
        if (GlobalConstants.ENV_PROD.equals(profile)) {
            String message = getMessage(ResultCode.SYSTEM_EXECUTION_ERROR);
            return R.failed(message);
        }
        return R.failed(ResultCode.REQUEST_CONTENT_TYPE_NOT_SUPPORTED, getMessage(ResultCode.REQUEST_CONTENT_TYPE_NOT_SUPPORTED));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingPathVariableException.class)
    public <T> R<T> handleException(MissingPathVariableException e) {
        log.error("缺少请求路径参数", e);
        if (GlobalConstants.ENV_PROD.equals(profile)) {
            String message = getMessage(ResultCode.SYSTEM_EXECUTION_ERROR);
            return R.failed(message);
        }
        return R.failed(ResultCode.REQUEST_PATH_PARAM_IS_BLANK, getMessage(ResultCode.REQUEST_PATH_PARAM_IS_BLANK));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public <T> R<T> handleException(MissingServletRequestParameterException e) {
        log.error("缺少请求参数", e);
        if (GlobalConstants.ENV_PROD.equals(profile)) {
            String message = getMessage(ResultCode.SYSTEM_EXECUTION_ERROR);
            return R.failed(message);
        }
        return R.failed(ResultCode.PARAM_IS_NULL, getMessage(ResultCode.PARAM_IS_NULL));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TypeMismatchException.class)
    public <T> R<T> handleException(TypeMismatchException e) {
        log.error("参数类型不匹配", e);
        if (GlobalConstants.ENV_PROD.equals(profile)) {
            String message = getMessage(ResultCode.SYSTEM_EXECUTION_ERROR);
            return R.failed(message);
        }
        return R.failed(ResultCode.REQUEST_PARAM_MISMATCH);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public <T> R<T> handleException(HttpMessageNotReadableException e) {
        log.error("请求内容不可读", e);
        if (GlobalConstants.ENV_PROD.equals(profile)) {
            String message = getMessage(ResultCode.SYSTEM_EXECUTION_ERROR);
            return R.failed(message);
        }
        return R.failed(ResultCode.REQUEST_CONTENT_NOT_READABLE, getMessage(ResultCode.REQUEST_CONTENT_NOT_READABLE));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotWritableException.class)
    public <T> R<T> handleException(HttpMessageNotWritableException e) {
        log.error("返回结果序列化异常", e);
        if (GlobalConstants.ENV_PROD.equals(profile)) {
            String message = getMessage(ResultCode.SYSTEM_EXECUTION_ERROR);
            return R.failed(message);
        }
        return R.failed(ResultCode.RESPONSE_RESULT_NOT_WRITABLE);
    }

    /**
     * ServletException
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ServletException.class)
    public <T> R<T> handleException(ServletException e) {
        log.error("Servlet异常", e.getMessage(), e);
        if (GlobalConstants.ENV_PROD.equals(profile)) {
            String message = getMessage(ResultCode.SYSTEM_EXECUTION_ERROR);
            return R.failed(message);
        }
        return R.failed(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public <T> R<T> handleException(BindException e) {
        log.warn("参数校验异常", e.getMessage());
        return wrapperBindingResult(e.getBindingResult());
    }

    /**
     * RequestParam参数的校验
     *
     * @param e
     * @param <T>
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public <T> R<T> handleException(ConstraintViolationException e) {
        log.warn("参数校验异常", e.getMessage());
        StringBuilder msg = new StringBuilder();
        for (ConstraintViolation error : e.getConstraintViolations()) {
            String queryParamPath = error.getPropertyPath().toString();
            String queryParam = queryParamPath.contains(".") ? queryParamPath.substring(queryParamPath.indexOf(".") + 1) : queryParamPath;
            msg.append(queryParam).append(":").append(error.getMessage());
        }
        return R.failed(ResultCode.PARAM_ERROR, msg.substring(0, msg.length() - 1));
    }

    /**
     * RequestBody参数的校验
     *
     * @param e
     * @param <T>
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public <T> R<T> handleException(MethodArgumentNotValidException e) {
        log.warn("参数校验异常", e);
        return wrapperBindingResult(e.getBindingResult());
    }

    /**
     * 包装绑定异常结果
     *
     * @param bindingResult 绑定结果
     * @return 异常结果
     */
    private R wrapperBindingResult(BindingResult bindingResult) {
        StringBuilder msg = new StringBuilder();
        for (ObjectError error : bindingResult.getAllErrors()) {
            if (msg.length() > 0) {
                msg.append(";");
            }
            if (error instanceof FieldError) {
                msg.append(((FieldError) error).getField()).append(":");
            }
            msg.append(error.getDefaultMessage() == null ? "" : error.getDefaultMessage());
        }
        return R.failed(ResultCode.PARAM_ERROR, msg.toString());
    }

    /**
     * MaxUploadSizeExceededException
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public <T> R<T> handleException(MaxUploadSizeExceededException e) {
        log.error("上传文件大小超出最大限制：{}", e.getMessage(), e);
        return R.failed(ResultCode.USER_UPLOAD_FILE_SIZE_EXCEEDS, getMessage(ResultCode.USER_UPLOAD_FILE_SIZE_EXCEEDS));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public <T> R<T> handleException(IllegalArgumentException e) {
        log.error("非法参数异常，异常原因：{}", e.getMessage(), e);
        if (GlobalConstants.ENV_PROD.equals(profile)) {
            // 当为生产环境, 不适合把具体的异常信息展示给用户, 比如404.
            String message = getMessage(ResultCode.SYSTEM_EXECUTION_ERROR);
            return R.failed(message);
        }
        return R.failed(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(JsonProcessingException.class)
    public <T> R<T> handleException(JsonProcessingException e) {
        log.error("Json转换异常，异常原因：{}", e.getMessage(), e);
        if (GlobalConstants.ENV_PROD.equals(profile)) {
            // 当为生产环境, 不适合把具体的异常信息展示给用户, 比如404.
            String message = getMessage(ResultCode.SYSTEM_EXECUTION_ERROR);
            return R.failed(message);
        }
        return R.failed(e.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(SQLSyntaxErrorException.class)
    public <T> R<T> handleException(SQLSyntaxErrorException e) {
        log.error("SQL语法错误", e);
        if (GlobalConstants.ENV_PROD.equals(profile)) {
            String message = getMessage(ResultCode.SYSTEM_EXECUTION_ERROR);
            return R.failed(message);
        }
        String errorMsg = e.getMessage();
        if (StrUtil.isNotBlank(errorMsg) && errorMsg.contains("denied to user")) {
            return R.failed("数据库用户无操作权限，建议本地搭建数据库环境");
        } else {
            return R.failed(String.format("SQL语句语法错误:%s", errorMsg));
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CompletionException.class)
    public <T> R<T> handleException(CompletionException e) {
        log.error(e.getMessage(), e);
        if (GlobalConstants.ENV_PROD.equals(profile)) {
            String message = getMessage(ResultCode.SYSTEM_EXECUTION_ERROR);
            return R.failed(message);
        }
        if (e.getMessage().startsWith("feign.FeignException")) {
            return R.failed("服务调用异常");
        }
        return handleException(e);
    }

//    如果是使用Feign作为服务间调用的中间件则启用以下代码，并增加feign相关依赖
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(FeignException.BadRequest.class)
//    public <T> R<T> handleException(FeignException.BadRequest e) {
//        log.info("微服务feign调用异常:{}", e.getMessage());
//        return R.failed(e.getMessage());
//    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BusinessException.class)
    public <T> R<T> handleException(BusinessException e) {
        return R.failed(e.getResultCode(), getMessage(e));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BaseException.class)
    public <T> R<T> handleException(BaseException e) {
        return R.failed(e.getResultCode(), getMessage(e));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public <T> R<T> handleException(Exception e) {
        log.error("未知异常,异常原因：{}", e.getMessage(), e);
        // 如果是生产环境，将具体错误信息展示给用户就不合适了
        if (GlobalConstants.ENV_PROD.equals(profile)) {
            String message = getMessage(new BaseException(ResultCode.SYSTEM_EXECUTION_ERROR));
            return R.failed(message);
        }
        return R.failed(e.getMessage());
    }

    /**
     * 传参类型错误时，用于消息转换
     *
     * @param throwable 异常
     * @return 错误信息
     */
    private String convertMessage(Throwable throwable) {
        String error = throwable.toString();
        String regulation = "\\[\"(.*?)\"]+";
        Pattern pattern = Pattern.compile(regulation);
        Matcher matcher = pattern.matcher(error);
        String group = "";
        if (matcher.find()) {
            String matchString = matcher.group();
            matchString = matchString.replace("[", "").replace("]", "");
            matchString = matchString.replaceAll("\\\"", "") + "字段类型错误";
            group += matchString;
        }
        return group;
    }
}
