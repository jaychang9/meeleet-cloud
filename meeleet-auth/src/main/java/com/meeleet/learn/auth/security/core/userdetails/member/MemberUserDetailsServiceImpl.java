package com.meeleet.learn.auth.security.core.userdetails.member;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.meeleet.learn.auth.security.userdetails.ExtUserDetailsService;
import com.meeleet.learn.common.constant.StringConstant;
import com.meeleet.learn.common.result.ResultCode;
import com.meeleet.learn.common.security.enums.AuthenticationIdentityEnum;
import com.meeleet.learn.ums.pojo.dto.UmsMemberAuthInfoDTO;
import com.meeleet.learn.ums.pojo.dto.UmsMemberDTO;
import com.meeleet.learn.ums.rpc.IUmsMemberDubboService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 商城会员用户认证服务
 *
 * @author jaychang
 */
@Service("memberUserDetailsService")
public class MemberUserDetailsServiceImpl implements ExtUserDetailsService {

    @DubboReference
    private IUmsMemberDubboService umsMemberDubboService;

    /**
     * 手机号码认证方式
     *
     * @param mobile
     * @return
     */
    public UserDetails loadUserByMobile(String mobile) {
        UmsMemberAuthInfoDTO member = umsMemberDubboService.loadUserByMobile(mobile);
        return createUserDetails(member, AuthenticationIdentityEnum.MOBILE);
    }

    /**
     * openid 认证方式
     *
     * @param openId
     * @return
     */
    public UserDetails loadUserByOpenid(String openid) {
        UmsMemberAuthInfoDTO member = umsMemberDubboService.loadUserByOpenid(openid);
        return createUserDetails(member, AuthenticationIdentityEnum.OPENID);
    }

    /**
     * username 认证方式
     *
     * @param username
     * @return
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UmsMemberAuthInfoDTO member = umsMemberDubboService.loadUserByUsername(username);
        return createUserDetails(member, AuthenticationIdentityEnum.USERNAME);
    }

    private UserDetails createUserDetails(UmsMemberAuthInfoDTO member, AuthenticationIdentityEnum authenticationIdentityEnum) {
        MemberUserDetails userDetails = null;
        if (null != member) {
            userDetails = new MemberUserDetails(member);
            // 认证身份标识
            userDetails.setAuthenticationIdentity(authenticationIdentityEnum.getValue());
        }
        if (userDetails == null) {
            throw new UsernameNotFoundException(ResultCode.USER_NOT_EXIST.getMsg());
        } else if (!userDetails.isEnabled()) {
            throw new DisabledException("该账户已被禁用!");
        } else if (!userDetails.isAccountNonLocked()) {
            throw new LockedException("该账号已被锁定!");
        } else if (!userDetails.isAccountNonExpired()) {
            throw new AccountExpiredException("该账号已过期!");
        }
        return userDetails;
    }

    @Override
    public void addUser(Map<String, Object> userInfo) {
        UmsMemberDTO umsMemberDTO = BeanUtil.mapToBean(userInfo, UmsMemberDTO.class, true, CopyOptions.create());
        umsMemberDTO.setUsername(StringConstant.USERNAME_PREFIX + umsMemberDTO.getMobile());
        umsMemberDubboService.addMember(umsMemberDTO);
    }
}
