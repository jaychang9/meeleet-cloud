package com.meeleet.cloud.common.validation.constraints.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.meeleet.cloud.common.validation.constraints.MobilePhone;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 校验手机号是否合法
 *
 * @author aaronuu
 */
public class MobilePhoneValueValidator implements ConstraintValidator<MobilePhone, String> {

	private Boolean required;

	@Override
	public void initialize(MobilePhone constraintAnnotation) {
		this.required = constraintAnnotation.required();
	}

	@Override
	public boolean isValid(String phoneValue, ConstraintValidatorContext context) {
		if (StrUtil.isEmpty(phoneValue)) {
			if (required) {
				return false;
			} else {
				return true;
			}
		} else {
			return ReUtil.isMatch(Validator.MOBILE, phoneValue);
		}
	}

}
