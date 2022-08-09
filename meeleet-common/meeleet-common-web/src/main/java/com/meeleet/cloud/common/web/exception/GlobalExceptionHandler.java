package com.meeleet.cloud.common.web.exception;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.meeleet.cloud.common.exception.BaseException;
import com.meeleet.cloud.common.exception.BusinessException;
import com.meeleet.cloud.common.i18n.UnifiedMessageSource;
import com.meeleet.cloud.common.result.IResultCode;
import com.meeleet.cloud.common.result.R;
import com.meeleet.cloud.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.ServletException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.util.concurrent.CompletionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 全局系统异常处理
 * 调整异常处理的HTTP状态码，丰富异常处理类型
 *
 * @author hxrui
 * @author Gadfly
 * @date 2020-02-25 13:54
 * <p>
 **/
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 生产环境
     */
    private final static String ENV_PROD = "prod";

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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public <T> R<T> handleException(BindException e) {
        log.warn("BindException:{}", e.getMessage());
        String msg = e.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("；"));
        return R.failed(ResultCode.PARAM_ERROR, msg);
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
        log.warn("ConstraintViolationException:{}", e.getMessage());
        String msg = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("；"));
        return R.failed(ResultCode.PARAM_ERROR, msg);
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
        log.warn("MethodArgumentNotValidException:{}", e.getMessage());
        String msg = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("；"));
        return R.failed(ResultCode.PARAM_ERROR, msg);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public <T> R<T> handleException(NoHandlerFoundException e) {
        log.error(e.getMessage(), e);
        return R.failed(ResultCode.RESOURCE_NOT_FOUND, getMessage(ResultCode.RESOURCE_NOT_FOUND));
    }

    /**
     * MissingServletRequestParameterException
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public <T> R<T> handleException(MissingServletRequestParameterException e) {
        log.error(e.getMessage(), e);
        return R.failed(ResultCode.PARAM_IS_NULL, getMessage(ResultCode.PARAM_IS_NULL));
    }

    /**
     * MethodArgumentTypeMismatchException
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public <T> R<T> handleException(MethodArgumentTypeMismatchException e) {
        log.error(e.getMessage(), e);
        return R.failed(ResultCode.PARAM_ERROR, getMessage(ResultCode.PARAM_ERROR));
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

    /**
     * ServletException
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ServletException.class)
    public <T> R<T> handleException(ServletException e) {
        log.error("ServletException:{}", e.getMessage(), e);
        if (ENV_PROD.equals(profile)) {
            // 当为生产环境, 不适合把具体的异常信息展示给用户, 比如404.
            String message = getMessage(ResultCode.SYSTEM_EXECUTION_ERROR);
            return R.failed(message);
        }
        return R.failed(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public <T> R<T> handleException(IllegalArgumentException e) {
        log.error("非法参数异常，异常原因：{}", e.getMessage(), e);
        if (ENV_PROD.equals(profile)) {
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
        if (ENV_PROD.equals(profile)) {
            // 当为生产环境, 不适合把具体的异常信息展示给用户, 比如404.
            String message = getMessage(ResultCode.SYSTEM_EXECUTION_ERROR);
            return R.failed(message);
        }
        return R.failed(e.getMessage());
    }

    /**
     * HttpMessageNotReadableException
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public <T> R<T> handleException(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        String errorMessage = "请求体不可为空";
        Throwable cause = e.getCause();
        if (cause != null) {
            errorMessage = convertMessage(cause);
        }
        return R.failed(errorMessage);
    }

    /**
     * TypeMismatchException
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TypeMismatchException.class)
    public <T> R<T> handleException(TypeMismatchException e) {
        log.error("类型不匹配:{}", e.getMessage(), e);
        if (ENV_PROD.equals(profile)) {
            String message = getMessage(ResultCode.SYSTEM_EXECUTION_ERROR);
            return R.failed(message);
        }
        return R.failed(e.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(SQLSyntaxErrorException.class)
    public <T> R<T> handleException(SQLSyntaxErrorException e) {
        log.error(e.getMessage(), e);
        if (ENV_PROD.equals(profile)) {
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
        return R.failed(e.getResultCode(),getMessage(e));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BaseException.class)
    public <T> R<T> handleException(BaseException e) {
        return R.failed(e.getResultCode(),getMessage(e));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public <T> R<T> handleException(Exception e) {
        log.error("未知异常,异常原因：{}", e.getMessage(), e);
        // 如果是生产环境，将具体错误信息展示给用户就不合适了
        if (ENV_PROD.equals(profile)) {
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
