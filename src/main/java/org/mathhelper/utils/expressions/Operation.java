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

    @NonNull
    private final Polynomial polynomial;

    @NonNull
    private Operator operator;

    @Nullable
    private Operation leftOperation;

    @ToString.Exclude
    private Operation rightOperation;

    public Operation(@NonNull Polynomial polynomial,
                     @NonNull Operator operator,
                     @Nullable Operation leftOperation) {
        this.polynomial = polynomial;
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
        NONE(3),
        ADDITION(2),
        SUBTRACTION(1),
        MULTIPLICATION(0),
        DIVISION(-1);

        private final int priority;

        Operator(int priority) {
            this.priority = priority;
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
