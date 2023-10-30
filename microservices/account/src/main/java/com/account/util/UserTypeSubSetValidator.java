package com.account.util;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

/**
 * Easy to handle exception
 */
public class UserTypeSubSetValidator implements ConstraintValidator<UserTypeSubset, UserType> {
    private UserType[] subset;

    @Override
    public void initialize(UserTypeSubset constraint) {
        this.subset = constraint.anyOf();
    }

    @Override
    public boolean isValid(UserType value, ConstraintValidatorContext context) {
        return value == null || Arrays.asList(subset).contains(value);
    }
}
