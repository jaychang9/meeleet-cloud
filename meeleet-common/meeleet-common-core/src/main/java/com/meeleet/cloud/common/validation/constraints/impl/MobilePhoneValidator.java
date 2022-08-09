package com.meeleet.cloud.common.validation.constraints.impl;


import cn.hutool.core.util.StrUtil;
import com.meeleet.cloud.common.validation.constraints.MobilePhone;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * <p>手机号码格式校验实现</p>
 *
 * @author jaychang
 */
public class MobilePhoneValidator implements ConstraintValidator<MobilePhone, String> {

    /**
     * 手机号码验证正则
     */
    private Pattern pattern = Pattern.compile("^1\\d{10}$");

    /**
     * 是否允许为空
     */
    private boolean isAllowedEmpty;

    @Override
    public void initialize(MobilePhone mobilePhone) {
        this.isAllowedEmpty = mobilePhone.allowedEmpty();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (isAllowedEmpty && StrUtil.isBlank(value)) {
            return true;
        }
        return pattern.matcher(value).matches();
    }
}
