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
        if (!expressionValidator.isValid(parts[0], context)) {
            context.buildConstraintViolationWithTemplate("Left side of equation is invalid: " + parts[0])
                    .addConstraintViolation();
            return false;
        }
        if (!expressionValidator.isValid(parts[1], context)) {
            context.buildConstraintViolationWithTemplate("Right side of equation is invalid: " + parts[1])
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

}