package org.mathhelper.utils.expressions;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;
import java.util.function.BinaryOperator;

@Data
@AllArgsConstructor
public class Operation implements Comparable<Operation> {

    @NotEmpty
    @NonNull
    private Map<Integer, Double> coefficients;
    @NotNull
    @NonNull
    private Operator operator;

    @Nullable
    private Operation leftOperation;

    @ToString.Exclude
    private Operation rightOperation;

    public Operation(@NonNull Map<Integer, Double> coefficients,
                     @NonNull Operator operator,
                     @Nullable Operation leftOperation) {
        this.coefficients = coefficients;
        this.operator = operator;
        this.leftOperation = leftOperation;
    }

    public void setLeftOperation(@Nullable Operation leftOperation) {
        this.leftOperation = leftOperation;
        if (leftOperation != null) {
            leftOperation.rightOperation = this;
        }
    }

    public void setRightOperation(@Nullable Operation rightOperation) {
        this.rightOperation = rightOperation;
        if (rightOperation != null) {
            rightOperation.leftOperation = this;
        }
    }

    @Getter
    public enum Operator {
        NONE(3, null),
        ADDITION(2, Double::sum),
        SUBTRACTION(1, (a, b) -> a - b),
        MULTIPLICATION(0, (a, b) -> a * b),
        DIVISION(-1, (a, b) -> a / b);

        private final int priority;

        private final BinaryOperator<Double> binaryOperator;

        Operator(int priority, BinaryOperator<Double> binaryOperator) {
            this.priority = priority;
            this.binaryOperator = binaryOperator;
        }

        public static Operator toOperator(char c) {
            return switch (c) {
                case '+' -> ADDITION;
                case '-' -> SUBTRACTION;
                case '*' -> MULTIPLICATION;
                case '/' -> DIVISION;
                default -> throw new IllegalArgumentException("Invalid operator");
            };
        }

    }

    @Override
    public int compareTo(@NonNull Operation o) {
        return operator.getPriority() - o.operator.getPriority();
    }

}
