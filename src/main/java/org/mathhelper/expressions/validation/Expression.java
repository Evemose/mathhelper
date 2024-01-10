package org.mathhelper.expressions.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ExpressionValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@NotNull
@NotEmpty
public @interface Expression {
    String message() default "Expression is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
