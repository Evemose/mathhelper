package org.mathhelper.utils;

import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mathhelper.utils.ExpressionUtils.Operation;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mathhelper.utils.ExpressionUtils.Operation.Operator.*;

public class ExpressionUtilsTest {

    @Test
    public void collapseOperationsTest_Addition() {
        var coefficients1 = new HashMap<Integer, Double>();
        coefficients1.put(1, 2.0);

        var coefficients2 = new HashMap<Integer, Double>();
        coefficients2.put(1, 3.0);

        var operation1 = new Operation(coefficients1, NONE, null);
        var operation2 = new Operation(coefficients2, ADDITION, operation1);

        var operations = new PriorityQueue<Operation>();
        operations.add(operation1);
        operations.add(operation2);

        ExpressionUtils.collapseOperations(operations);

        var resultantOperation = operations.poll();
        assertEquals(5.0, resultantOperation.getCoefficients().get(1));
    }

    @Test
    public void collapseOperationsTest_NoOperation() {
        var coefficients = new HashMap<Integer, Double>();
        coefficients.put(1, 2.0);

        var operation = new Operation(coefficients, NONE, null);

        var operations = new PriorityQueue<Operation>();
        operations.add(operation);

        ExpressionUtils.collapseOperations(operations);

        var resultantOperation = operations.poll();
        assertEquals(2.0, resultantOperation.getCoefficients().get(1));
    }

    @Test
    public void testCollapseOperations() {
        var coefficients = new HashMap<Integer, Double>();
        coefficients.put(1, 2.0);
        coefficients.put(0, 3.0);
        var operator = new ExpressionUtils.Operation(coefficients,
                ExpressionUtils.Operation.Operator.ADDITION,
                null);

        var operations = new PriorityQueue<ExpressionUtils.Operation>();
        operations.add(operator);

        ExpressionUtils.collapseOperations(operations);

        assertEquals(1, operations.size());
        var result = operations.peek();
        assertEquals(ExpressionUtils.Operation.Operator.ADDITION, result.getOperator());
        assertEquals(coefficients, result.getCoefficients());
        assertEquals(null, result.getLeftOperation());
    }

    @ParameterizedTest
    @MethodSource("getOperations")
    public void testCollapseOperationsWithMultipleOperations(PriorityQueue<Operation> operations) {
        ExpressionUtils.collapseOperations(operations);
        assertEquals(1, operations.size());
        var resultantOperation = operations.peek();
        assertNotNull(resultantOperation);
        assertEquals(NONE, resultantOperation.getOperator());
        assertThat(resultantOperation.getCoefficients()).containsExactlyInAnyOrderEntriesOf(
               Map.of(0, -40.0, 1, 37.0, 2, 56.0));
    }

    private static Stream<PriorityQueue<Operation>> getOperations() {
        return Stream.of(
                getOperationsWithMultiplication(),
                getComplexOperation1()
        );
    }

    private static PriorityQueue<Operation> getComplexOperation1() {
        var coefficients1 = new HashMap<>(Map.of(1, 2.0, 0, -8.3));
        var operation1 = new Operation(coefficients1, NONE, null);

        var coefficients2 = new HashMap<>(Map.of(1, 4.0, 0, -5.0));
        var operation2 = new Operation(coefficients2,
                DIVISION, operation1);

        var coefficients3 = new HashMap<>(Map.of(0, -2.5, 1, -8.0));
        var operation3 = new Operation(coefficients3,
                SUBTRACTION, operation2);

        var coefficients4 = new HashMap<>(Map.of(1, -7.0, 0, 8.0));
        var operation4 = new Operation(coefficients4,
                MULTIPLICATION, operation3);

        var coefficients5 = new HashMap<>(Map.of(1, 9.0, 0, 10.0));
        var operation5 = new Operation(coefficients5,
                ADDITION, operation4);

        operation1.setRightOperation(operation2);
        operation2.setRightOperation(operation3);
        operation3.setRightOperation(operation4);
        operation4.setRightOperation(operation5);

        var operations = new PriorityQueue<Operation>();
        operations.add(operation1);
        operations.add(operation2);
        operations.add(operation3);
        operations.add(operation4);
        operations.add(operation5);
        return operations;
    }

    private static PriorityQueue<Operation> getOperationsWithMultiplication() {
        var coefficients1 = new HashMap<>(Map.of(1, 2.0, 0, 3.0));
        var operation1 = new Operation(coefficients1, NONE, null);

        var coefficients2 = new HashMap<>(Map.of(1, 4.0, 0, -5.0));
        var operation2 = new Operation(coefficients2,
                ADDITION, operation1);

        var coefficients3 = new HashMap<>(Map.of(0, 6.0, 1, -8.0));
        var operation3 = new Operation(coefficients3,
                SUBTRACTION, operation2);

        var coefficients4 = new HashMap<>(Map.of(1, 7.0, 0, 8.0));
        var operation4 = new Operation(coefficients4,
                MULTIPLICATION, operation3);

        var coefficients5 = new HashMap<>(Map.of(1, 9.0, 0, 10.0));
        var operation5 = new Operation(coefficients5,
                ADDITION, operation4);

        operation1.setRightOperation(operation2);
        operation2.setRightOperation(operation3);
        operation3.setRightOperation(operation4);
        operation4.setRightOperation(operation5);

        var operations = new PriorityQueue<Operation>();
        operations.add(operation1);
        operations.add(operation2);
        operations.add(operation3);
        operations.add(operation4);
        operations.add(operation5);
        return operations;
    }
}