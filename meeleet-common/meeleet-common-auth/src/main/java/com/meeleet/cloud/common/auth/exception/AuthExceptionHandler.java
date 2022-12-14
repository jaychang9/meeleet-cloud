package com.meeleet.cloud.common.auth.exception;

import com.meeleet.cloud.common.result.R;
import com.meeleet.cloud.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class AuthExceptionHandler {

    /**
     * 用户不存在
     *
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UsernameNotFoundException.class)
    public R handleUsernameNotFoundException(UsernameNotFoundException e) {
        // 为了系统安全，模糊错误提示
        return R.failed(ResultCode.USERNAME_OR_PASSWORD_ERROR);
    }

    /**
     * 用户名或密码错误(这里发现ResourceOwnerPasswordTokenGranter处理UsernameNotFoundException时会将异常转为InvalidGrantException)
     *
     * @See org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter#getOAuth2Authentication(org.springframework.security.oauth2.provider.ClientDetails, org.springframework.security.oauth2.provider.TokenRequest)
     *
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidGrantException.class)
    public R handleInvalidGrantException(InvalidGrantException e) {
        if ("Bad credentials".equals(e.getMessage())) {
            return R.failed(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }
        return R.failed(e.getMessage());
    }

    /**
     * 不支持的认证模式
     *
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnsupportedGrantTypeException.class)
    public R handleUnsupportedGrantTypeException(UnsupportedGrantTypeException e) {
        return R.failed(ResultCode.UNSUPPORTED_GRANT_TYPE);
    }

    /**
     * 未授权的认证类型
     *
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({InvalidClientException.class})
    public R handleInvalidClientException(InvalidClientException e) {
        String message = e.getMessage();
        if (StringUtils.hasText(message) && message.startsWith("Unauthorized grant type:") ) {
            return R.failed(ResultCode.UNAUTHORIZED_GRANT_TYPE);
        }
        return R.failed(e.getMessage());
    }

    /**
     * 账户异常(禁用、锁定、过期)
     *
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({InternalAuthenticationServiceException.class})
    public R handleInternalAuthenticationServiceException(InternalAuthenticationServiceException e) {
        return R.failed(e.getMessage());
    }

    /**
     * token 无效或已过期
     *
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({InvalidTokenException.class})
    public R handleInvalidTokenExceptionException(InvalidTokenException e) {
        // access_token,refresh_token无效或过期都会进到这里来
        return R.failed(ResultCode.TOKEN_INVALID_OR_EXPIRED);
    }

    /**
     * scope 无效
     *
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({InvalidScopeException.class})
    public R handleInvalidScopeException(InvalidScopeException e) {
        return R.failed(ResultCode.INVALID_SCOPE);
    }


    /**
     * token 无效或已过期
     *
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({Exception.class})
    public R handleException(Exception e) {
        log.warn("未知异常：{}",e.getMessage(),e);
        return R.failed(e.getMessage());
    }

}
