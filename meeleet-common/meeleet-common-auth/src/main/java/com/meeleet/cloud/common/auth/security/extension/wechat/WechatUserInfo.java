package com.meeleet.cloud.common.auth.security.extension.wechat;

import lombok.Data;

/**
 * 微信用户信息
 *
 * @author jaychang
 */
@Data
public class WechatUserInfo {
    /** 头像*/
    private String avatarUrl;

    /** 城市*/
    private String city;

    /** 国家*/
    private String country;

    /** 性别*/
    private Integer gender;

    /** 语言*/
    private String language;

    /** 昵称*/
    private String nickName;

    /** 省份*/
    private String province;

}
