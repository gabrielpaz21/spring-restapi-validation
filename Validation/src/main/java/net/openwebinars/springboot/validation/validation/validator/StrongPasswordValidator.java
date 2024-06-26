package net.openwebinars.springboot.validation.validation.validator;

import net.openwebinars.springboot.validation.validation.annotation.StrongPassword;
import org.passay.*;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    int min, max;
    boolean upper, lower, number, alpha, special;


    @Override
    public void initialize(StrongPassword constraintAnnotation) {
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
        upper = constraintAnnotation.hasUpper();
        lower = constraintAnnotation.hasLower();
        number = constraintAnnotation.hasNumber();
        alpha = constraintAnnotation.hasAlpha();
        special = constraintAnnotation.hasSpecial();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {

        List<Rule> rules = new ArrayList<>();

        rules.add(new LengthRule(min, max));

        if (alpha) {
            rules.add(new CharacterRule(EnglishCharacterData.Alphabetical, 1));

            // Solamente aplicamos estas reglas sobre caracteres alfabéticos
            // si alpha == true

            if (upper)
                rules.add(new CharacterRule(EnglishCharacterData.UpperCase, 1));

            if (lower)
                rules.add(new CharacterRule(EnglishCharacterData.LowerCase, 1));


        }

        if (number)
            rules.add(new CharacterRule(EnglishCharacterData.Digit, 1));


        if (special)
            rules.add(new CharacterRule(EnglishCharacterData.Special, 1));

        PasswordValidator passwordValidator = new PasswordValidator(rules);

        RuleResult result =  passwordValidator.validate(new PasswordData(s));

        if (result.isValid())
            return true;

        List<String> messages = passwordValidator.getMessages(result);
        String template = String.join(",", messages);
        constraintValidatorContext.buildConstraintViolationWithTemplate(template)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
        return false;

    }
}
