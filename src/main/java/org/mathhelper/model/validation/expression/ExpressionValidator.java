package org.mathhelper.model.validation.expression;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class ExpressionValidator implements ConstraintValidator<Expression, String> {

    @Override
    public void initialize(Expression constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        value = value.replaceAll(" ", "");
        context.disableDefaultConstraintViolation();
        if (!isParenthesesValid(value)) {
            context.buildConstraintViolationWithTemplate("Parentheses are not balanced: " + value)
                    .addConstraintViolation();
            return false;
        }
        if (!isExpressionValid(value)) {
            context.buildConstraintViolationWithTemplate("Expression is not valid: " + value)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean isParenthesesValid(String equation) {
        var balance = 0;
        for (var c : equation.toCharArray()) {
            if (c == '(') {
                balance++;
            } else if (c == ')') {
                if (balance == 0) {
                    return false;
                }
                balance--;
            } else if (c == '=' && balance != 0) {
                return false;
            }
        }
        return balance == 0;
    }

    private boolean isExpressionValid(String equation) {
        final var equationPattern =
                "^((-\\(+)|\\(+|(\\(+-)|-)?(\\d+(\\.\\d+)?|x)(((\\(+-)|([-+*/](\\(*-?|\\)*)?)|(\\)+[-+*/]))\\(*(\\d+(\\.\\d+)?|x))*\\)*$";
        return equation.matches(equationPattern);
    }
}
