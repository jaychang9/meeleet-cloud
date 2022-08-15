package com.meeleet.cloud.common.validation.constraints.impl;

import cn.hutool.core.util.StrUtil;
import com.meeleet.cloud.common.enums.YesOrNotEnum;
import com.meeleet.cloud.common.validation.constraints.FlagValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 校验标识，只有Y和N两种状态的标识
 *
 * @author aaronuu
 */
public class FlagValueValidator implements ConstraintValidator<FlagValue, String> {

	private Boolean required;

	@Override
	public void initialize(FlagValue constraintAnnotation) {
		this.required = constraintAnnotation.required();
	}

	@Override
	public boolean isValid(String flagValue, ConstraintValidatorContext context) {

		// 如果是必填的
		if (required) {
			return YesOrNotEnum.Y.getValue().equals(flagValue) || YesOrNotEnum.N.getValue().equals(flagValue);
		} else {

			//如果不是必填，可以为空
			if (StrUtil.isEmpty(flagValue)) {
				return true;
			} else {
				return YesOrNotEnum.Y.getValue().equals(flagValue) || YesOrNotEnum.N.getValue().equals(flagValue);
			}
		}
	}
}
