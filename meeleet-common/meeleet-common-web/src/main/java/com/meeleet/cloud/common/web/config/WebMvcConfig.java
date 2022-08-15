package com.meeleet.cloud.common.web.config;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MessageSourceResourceBundleLocator;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@Configuration
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public Validator validator(final AutowireCapableBeanFactory autowireCapableBeanFactory, final MessageSource messageSource) {
        // 设置 messageSource 属性，实现 i18 国际化
        ResourceBundleMessageInterpolator resourceBundleMessageInterpolator = new ResourceBundleMessageInterpolator(new MessageSourceResourceBundleLocator(messageSource));

        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .failFast(true) // failFast=true 不校验所有参数，只要出现校验失败情况直接返回，不再进行后续参数校验
                .constraintValidatorFactory(new SpringConstraintValidatorFactory(autowireCapableBeanFactory))
                .messageInterpolator(resourceBundleMessageInterpolator)
                .buildValidatorFactory();

        return validatorFactory.getValidator();
    }
}
