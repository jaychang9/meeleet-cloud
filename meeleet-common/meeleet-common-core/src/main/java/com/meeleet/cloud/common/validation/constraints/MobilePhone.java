	package com.meeleet.cloud.common.validation.constraints;

import com.meeleet.cloud.common.validation.constraints.impl.MobilePhoneValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


    /**
     *<p>校验手机号码格式的注解</p>
     *
     * @author jaychang
     */
    @Documented
    @Constraint(validatedBy = {MobilePhoneValidator.class})
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Repeatable(MobilePhone.List.class)
    public @interface MobilePhone {

        /** 是否允许为空*/
        boolean allowedEmpty() default false;

        String message() default "手机号格式不合法";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

        @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
        @Retention(RUNTIME)
        @Documented
        public @interface List {
            MobilePhone[] value();
        }

    }