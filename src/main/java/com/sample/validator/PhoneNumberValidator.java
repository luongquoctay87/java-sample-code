package com.sample.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<ValidatePhoneNumber, String> {

    private final String PATTERN = "(country-code)[0-9]{8}";

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {

        if (Pattern.matches(PATTERN, phone)) {
            return true;
        }

        return false;

    }
}
