package org.mathhelper.utils;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.mathhelper.model.validation.expression.Expression;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.regex.Pattern;

@UtilityClass
public class ExpressionUtils {

    @Builder
    public record Operation(Map<Integer, Double> coefficients, Operator operator) implements Comparable<Operation> {

        @Getter
        public enum Operator {
            ADDITION(0),
            SUBTRACTION(0),
            MULTIPLICATION(1),
            DIVISION(1);

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

    public static PriorityQueue<Operation> parseExpression(@Expression String expression) {
        var operations = new PriorityQueue<Operation>();
        final var pattern = "[-+*/]-?(\\(*[0-9]+\\)*)?";
        var matcher = Pattern.compile(pattern).matcher(expression);
        while (matcher.find()) {
            var group = matcher.group();
            var operator = Operation.Operator.toOperator(group.charAt(0));
            var operationBuilder = Operation.builder().operator(operator);
            for (var i = 1; i < group.length(); i++) {
                var c = group.charAt(i);
                if (c == '(') {
                    var closingParenthesisIndex = findClosingParenthesisIndex(group, i);
                    var parenthesisExpression = group.substring(i + 1, closingParenthesisIndex);
                    var parenthesisOperations = parseExpression(parenthesisExpression);
                    var parenthesisCoefficient = parenthesisOperations.stream()
                            .mapToDouble(o -> o.coefficients().values().stream().mapToDouble(Double::doubleValue).sum())
                            .sum();
                    operationBuilder.coefficients(Map.of(0, parenthesisCoefficient));
                    i = closingParenthesisIndex;
                } else if (Character.isDigit(c)) {
                    var coefficient = Double.parseDouble(group.substring(i));
                    operationBuilder.coefficients(Map.of(0, coefficient));
                    break;
                }
            }
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