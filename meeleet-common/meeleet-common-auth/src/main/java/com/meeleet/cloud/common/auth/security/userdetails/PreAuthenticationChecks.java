
package com.meeleet.cloud.common.auth.security.userdetails;

import com.meeleet.cloud.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


/**
 * 自定义账号状态检查
 *
 * @author owen 624191343@qq.com
 * blog: https://blog.51cto.com/13005375
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Slf4j
public class PreAuthenticationChecks implements UserDetailsChecker {

    @Override
    public void check(UserDetails userDetails) {
        if (userDetails == null) {
            if (log.isDebugEnabled()) {
                log.debug("User not found");
            }
            throw new UsernameNotFoundException(ResultCode.USER_NOT_EXIST.getMessage());
        }
        if (!userDetails.isEnabled()) {
            if (log.isDebugEnabled()) {
                log.debug("User[{}] account is disabled", userDetails.getUsername());
            }
            throw new DisabledException("该账户已被禁用!");
        }
        if (!userDetails.isAccountNonLocked()) {
            if (log.isDebugEnabled()) {
                log.debug("User[{}] account is locked", userDetails.getUsername());
            }
            throw new LockedException("该账号已被锁定!");
        }
        if (!userDetails.isAccountNonExpired()) {
            if (log.isDebugEnabled()) {
                log.debug("User[{}] account is expired", userDetails.getUsername());
            }
            throw new AccountExpiredException("该账号已过期!");
        }
    }
}
