package com.meeleet.cloud.common.validation.constraints.impl;


import com.meeleet.cloud.common.validation.constraints.FieldMatch;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

/**
 * <p>字段匹配校验器</p>
 *
 * @author jaychang
 */
public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;


    @Override
    public void initialize(final FieldMatch constraintAnnotation)
    {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
    }


    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context)
    {
        try {
            final Field firstField = value.getClass().getDeclaredField(firstFieldName);
            firstField.setAccessible(true);
            final Field secondField = value.getClass().getDeclaredField(secondFieldName);
            secondField.setAccessible(true);

            final Object firstObj = firstField.get(value);
            final Object secondObj = secondField.get(value);

            return firstObj == null && secondObj == null || firstObj != null && firstObj.equals(secondObj);
        }
        catch (final Exception ignore)
        {
            // ignore
        }
        return true;
    }}