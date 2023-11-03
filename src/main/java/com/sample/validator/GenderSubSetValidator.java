package com.sample.validator;

import com.sample.utils.Gender;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

/**
 * Easy to handle exception
 */
public class GenderSubSetValidator implements ConstraintValidator<GenderSubset, Gender> {
    private Gender[] subset;

    @Override
    public void initialize(GenderSubset constraint) {
        this.subset = constraint.anyOf();
    }

    @Override
    public boolean isValid(Gender value, ConstraintValidatorContext context) {
        return value == null || Arrays.asList(subset).contains(value);
    }
}
