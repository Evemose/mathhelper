package org.mathhelper.model.validation.equation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.mathhelper.model.validation.expression.ExpressionValidator;
import org.springframework.stereotype.Component;

@Component
public class EquationValidator implements ConstraintValidator<EquationConstraint, String> {

    private final ExpressionValidator expressionValidator = new ExpressionValidator();

    @Override
    public void initialize(EquationConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        value = value.replaceAll(" ", "");
        context.disableDefaultConstraintViolation();
        final var parts = value.split("=");
        if (parts.length != 2) {
            context.buildConstraintViolationWithTemplate("Equation must have exactly one '=' sign")
                    .addConstraintViolation();
            return false;
        }
        return expressionValidator.isValid(parts[0], context) && expressionValidator.isValid(parts[1], context);
    }

}