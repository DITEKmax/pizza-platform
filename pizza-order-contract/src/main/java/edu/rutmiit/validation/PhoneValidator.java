package edu.rutmiit.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {

    private static final Pattern PHONE = Pattern.compile("^(\\+7|8)(\\d{10})$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context){
        if (value == null || value.isBlank()){
            return true;
        }
        String clear = value.replaceAll("[\\s()\\-]", "");
        return PHONE.matcher(clear).matches();
    }
}
