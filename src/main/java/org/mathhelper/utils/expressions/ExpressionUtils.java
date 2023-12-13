package org.mathhelper.utils.expressions;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.mathhelper.model.validation.expression.Expression;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.regex.Pattern;

@Component
@Validated
public class ExpressionUtils {

    public Polynomial parseExpression(@Expression String expression) {
        expression = expression.replaceAll(" ", "");
        var operations = initializeOperations(expression);
        return Objects.requireNonNull(collapseOperations(operations));
    }

    public Polynomial collapseOperations(Collection<Operation> operationCollection) {
        var operations = new PriorityQueue<>(operationCollection.size(), getOperationsInexpressionComparator());
        operations.addAll(operationCollection);
        while (operations.size() > 1) {
            var operation = operations.poll();
            var leftOperation = operation.getLeftOperation();
            var leftOperationPolynomial = Objects.requireNonNull(leftOperation).getPolynomial();
            var operationPolynomial = operation.getPolynomial();
            var operationOperator = operation.getOperator();
            switch (operationOperator) {
                case DIVISION -> leftOperationPolynomial.divide(operationPolynomial);
                case MULTIPLICATION -> leftOperationPolynomial.multiply(operationPolynomial);
                case ADDITION -> leftOperationPolynomial.add(operationPolynomial);
                case SUBTRACTION -> leftOperationPolynomial.subtract(operationPolynomial);
                default -> throw new IllegalStateException("Unexpected value: " + operationOperator);
            }
            leftOperation.setRightOperation(operation.getRightOperation());
        }
        return Objects.requireNonNull(operations.poll()).getPolynomial();
    }

    private PriorityQueue<Operation> initializeOperations(String expression) {
        expression = '+' + expression;
        var operations = new PriorityQueue<>(getOperationsInexpressionComparator());
        final var pattern = "[-+*/]-?(\\(+-?((\\d+(\\.\\d+)?)|x)((([-+*/]-?((\\d+(\\.\\d+)?)|x))\\)*)*\\)+)|((\\d+(\\.\\d+)?)|x))";
        var matcher = Pattern.compile(pattern).matcher(expression);
        Operation previousOperation = null;
        while (matcher.find()) {
            var group = matcher.group();
            var operator = matcher.start() == 0 ? Operation.Operator.NONE : Operation.Operator.toOperator(group.charAt(0));
            var operationBuilder = Operation.builder().operator(operator).leftOperation(previousOperation);
            if (group.contains("(")) {
                var reverseSigns = group.length() > 2 && group.charAt(1) == '-';
                var parenthesisIndex = reverseSigns ? 2 : 1;
                var closingParenthesisIndex = findClosingParenthesisIndex(group, parenthesisIndex);
                var subExpressionPolynomial = parseExpression(group.substring(parenthesisIndex + 1, closingParenthesisIndex));
                operationBuilder.polynomial(subExpressionPolynomial);
                if (reverseSigns) {
                    subExpressionPolynomial.multiply(-1);
                }
            } else {
                var coefficients = new HashMap<Integer, Double>();
                if (group.contains("x")) {
                    var coefficient = group.length() > 2 ? -1d : 1;
                    coefficients.put(1, coefficient);
                } else {
                    var coefficient = Double.parseDouble(group.substring(1));
                    coefficients.merge(0, coefficient, Double::sum);
                }
                operationBuilder.polynomial(new Polynomial(coefficients));
            }
            var operation = operationBuilder.build();
            operations.add(operation);
            previousOperation = operation;
        }
        return operations;
    }

    @NonNull
    private Comparator<Operation> getOperationsInexpressionComparator() {
        return (o1, o2) -> {
            var o1OperatorPriority = switch (o1.getOperator()) {
                case NONE -> 3;
                case ADDITION -> 2;
                case SUBTRACTION -> 1;
                case MULTIPLICATION -> 0;
                case DIVISION -> -1;
            };
            var o2OperatorPriority = switch (o2.getOperator()) {
                case NONE -> 3;
                case ADDITION -> 2;
                case SUBTRACTION -> 1;
                case MULTIPLICATION -> 0;
                case DIVISION -> -1;
            };
            return o1OperatorPriority - o2OperatorPriority;
        };
    }

    private int findClosingParenthesisIndex(String expression, int openingParenthesisIndex) {
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
        throw new IllegalArgumentException("No closing parenthesis found: " + expression);
    }
}