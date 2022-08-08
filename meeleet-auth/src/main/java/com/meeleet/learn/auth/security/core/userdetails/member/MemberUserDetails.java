package com.meeleet.learn.auth.security.core.userdetails.member;

import com.meeleet.learn.common.constant.GlobalConstants;
import com.meeleet.learn.common.security.enums.AuthenticationIdentityEnum;
import com.meeleet.learn.ums.pojo.dto.UmsMemberAuthInfoDTO;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;


/**
 * 用户认证信息
 *
 * @author jaychang
 * @date 2021/9/27
 */
@Data
public class MemberUserDetails implements UserDetails {

    private Long memberId;
    private String username;
    /** 仅密码模式时用到*/
    private String password;
    private Boolean enabled;

    /**
     * 扩展字段：认证身份标识，枚举值如下：
     *
     * @see AuthenticationIdentityEnum
     */
    private String authenticationIdentity;

    /**
     * 小程序会员用户体系
     *
     * @param member 小程序会员用户认证信息
     */
    public MemberUserDetails(UmsMemberAuthInfoDTO member) {
        this.setMemberId(member.getMemberId());
        this.setUsername(member.getUsername());
        this.setPassword(member.getPassword());
        this.setEnabled(GlobalConstants.STATUS_YES.equals(member.getStatus()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new HashSet<>();
        return collection;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
