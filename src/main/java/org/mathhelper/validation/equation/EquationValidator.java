package org.mathhelper.validation.equation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class EquationValidator implements ConstraintValidator<Equation, String> {
    @Override
    public void initialize(Equation constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        if (!isParenthesesValid(value)) {
            context.buildConstraintViolationWithTemplate("Parentheses are not balanced")
                    .addConstraintViolation();
            return false;
        }
        if (!isEquationValid(value)) {
            context.buildConstraintViolationWithTemplate("Equation is not valid according to the pattern")
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

    private boolean isEquationValid(String equation) {
        final var equationPattern = "^((-\\(+)|\\(+|(\\(+-)|-)?(\\d+(\\.\\d+)?|x)(((\\(+-)|([-+*/](\\(+-?|\\)*)?)|(\\)+[-+*/]))(\\d+(\\.\\d+)?|x))*\\)*" +
                "=((-\\(+)|\\(+|(\\(+-)|-)?(\\d+(\\.\\d+)?|x)(((\\(+-)|([-+*/](\\(+-?|\\)*)?)|(\\)+[-+*/]))(\\d+(\\.\\d+)?|x))*\\)*$";
        return equation.matches(equationPattern);
    }
}