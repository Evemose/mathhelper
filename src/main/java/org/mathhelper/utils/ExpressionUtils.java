package org.mathhelper.utils;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.UtilityClass;
import org.mathhelper.model.validation.expression.Expression;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.regex.Pattern;

@UtilityClass
public class ExpressionUtils {

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

    public static PriorityQueue<Operation> parseExpression(@Expression String expression) {
        var operations = initializeOperations(expression);

        return operations;
    }

    public static void collapseOperations(PriorityQueue<Operation> operations) {
        while (operations.size() > 1) {
            var operation = operations.poll();
            var leftOperation = operation.getLeftOperation();
            var leftOperationCoefficients = leftOperation.getCoefficients();
            var operationCoefficients = operation.getCoefficients();
            var operationOperator = operation.getOperator();
            var binaryOperator = operationOperator.getBinaryOperator();
            switch (operationOperator) {
                case DIVISION -> {
                    var backupMap = new HashMap<>(leftOperationCoefficients);
                    leftOperationCoefficients.clear();
                    for (var leftEntry : backupMap.entrySet()) {
                        for (var entry : operationCoefficients.entrySet()) {
                            leftOperationCoefficients.merge(
                                    leftEntry.getKey() - entry.getKey(),
                                    leftEntry.getValue() / entry.getValue(), Double::sum);
                        }
                    }
                }
                case MULTIPLICATION -> {
                    var backupMap = new HashMap<>(leftOperationCoefficients);
                    leftOperationCoefficients.clear();
                    for (var leftEntry : backupMap.entrySet()) {
                        for (var entry : operationCoefficients.entrySet()) {
                            leftOperationCoefficients.merge(
                                    leftEntry.getKey() + entry.getKey(),
                                    entry.getValue() * leftEntry.getValue(), Double::sum);
                        }
                    }
                }
                default -> {
                    for (var entry : operationCoefficients.entrySet()) {
                        if (leftOperationCoefficients.containsKey(entry.getKey())) {
                            leftOperationCoefficients.merge(entry.getKey(), entry.getValue(), binaryOperator);
                        } else {
                            leftOperationCoefficients.put(entry.getKey(), binaryOperator.apply(0.d, entry.getValue()));
                        }
                    }
                }
            }
            leftOperation.setRightOperation(operation.getRightOperation());
        }
    }

    private static PriorityQueue<Operation> initializeOperations(String expression) {
        var operations = new PriorityQueue<Operation>();
        final var pattern = "[-+*/]-?(\\(*[0-9]+\\)*)?";
        var matcher = Pattern.compile(pattern).matcher(expression);
        Operation previousOperation = null;
        while (matcher.find()) {
            var group = matcher.group();
            var operator = matcher.start() == 0 ? Operation.Operator.NONE : Operation.Operator.toOperator(group.charAt(0));
            var operation = new Operation(Map.of(), operator, previousOperation);
            var coefficients = new HashMap<Integer, Double>();
            var c = group.charAt(1);
            if (c == '(') {
                var closingParenthesisIndex = findClosingParenthesisIndex(group, matcher.start());
                var subExpressionCoefficients = evaluateExpression(group.substring(matcher.start() + 1, closingParenthesisIndex));
                for (var entry : subExpressionCoefficients.entrySet()) {
                    var exponent = entry.getKey();
                    var coefficient = entry.getValue();
                    coefficients.merge(exponent, coefficient, Double::sum);
                }
            } else {
                if (c == 'x') {
                    coefficients.merge(1, 1.0, Double::sum);
                } else if (group.length() > 2 && group.charAt(2) == 'x') {
                    coefficients.merge(1, -1.0, Double::sum);
                } else {
                    var coefficient = Double.parseDouble(group.substring(matcher.start() == 0 ? 0 : 1));
                    coefficients.merge(0, coefficient, Double::sum);
                }
            }
            operation.setCoefficients(coefficients);
            operation.setLeftOperation(previousOperation);
            operations.add(previousOperation);
        }
        return operations;
    }

    public static Map<Integer, Double> evaluateExpression(@Expression String expression) {
        var coefficients = new HashMap<Integer, Double>();
        var operations = parseExpression(expression);
        return coefficients;
    }

    private static int findClosingParenthesisIndex(String expression, int openingParenthesisIndex) {
        var parenthesisCount = 1;
        for (var i = openingParenthesisIndex + 1; i < expression.length(); i++) {
            var c = expression.charAt(i);
            if (c == '(') {
                parenthesisCount++;
            } else if (c == ')') {
                parenthesisCount--;
            }
            if (parenthesisCount == 0) {
                return i;
            }
        }
        throw new IllegalArgumentException("No closing parenthesis found");
    }
}