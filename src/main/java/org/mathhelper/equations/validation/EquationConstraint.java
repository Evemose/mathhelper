package org.mathhelper.equations.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to specify that a field or parameter should be validated with the EquationValidator.
 * The EquationValidator checks whether the equation is valid according to the specified pattern and if the parentheses are balanced.
 */
@Constraint(validatedBy = EquationValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@NotNull
@NotEmpty
public @interface EquationConstraint {
    String message() default "Equation is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
