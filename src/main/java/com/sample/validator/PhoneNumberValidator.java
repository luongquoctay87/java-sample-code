package com.sample.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

import static com.sample.utils.Constant.REGEX.NUMBER_MULTIPLE_REGEX;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {
    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        return Pattern.matches(NUMBER_MULTIPLE_REGEX, phone);
    }
}
