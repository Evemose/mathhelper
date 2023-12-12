package org.mathhelper.utils.expressions;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mathhelper.utils.expressions.Operation.Operator.*;

public class ExpressionUtilsTest {

    @Test
    public void collapseOperationsTest_NoOperation() {
        var operation = new Operation(new Polynomial(new HashMap<>(Map.of(1, 2.0))), NONE, null);

        var operations = new PriorityQueue<Operation>();
        operations.add(operation);

        ExpressionUtils.collapseOperations(operations);

        var resultantOperation = operations.poll();
        assertNotNull(resultantOperation);
        assertEquals(2.0, resultantOperation.getPolynomial().getNumeratorCoefficients().get(0));
    }

    @Test
    public void testCollapse_ComplexExpression() {
        var operations = getComplexExpression();
        ExpressionUtils.collapseOperations(operations);
        assertEquals(1, operations.size());
        var resultantOperation = operations.peek();
        assertNotNull(resultantOperation);
        assertEquals(NONE, resultantOperation.getOperator());
        assertThat(resultantOperation.getPolynomial().getNumeratorCoefficients()).containsExactlyInAnyOrderEntriesOf(
               Map.of(0, -158.3, 1, -155.5, 2, 502d, 3, -224d));
        assertThat(resultantOperation.getPolynomial().getDenominatorCoefficients()).containsExactlyInAnyOrderEntriesOf(
                Map.of(0, -5d, 1, 4d));
    }

    private static PriorityQueue<Operation> getComplexExpression() {
        var operation1 = new Operation(new Polynomial(new HashMap<>(Map.of(1, 2.0, 0, -8.3))),
                NONE, null);
        var operation2 = new Operation(new Polynomial(new HashMap<>(Map.of(1, 4.0, 0, -5.0))),
                DIVISION, operation1);
        var operation3 = new Operation(new Polynomial(new HashMap<>(Map.of(0, -2.5, 1, -8.0))),
                SUBTRACTION, operation2);
        var operation4 = new Operation(new Polynomial(new HashMap<>(Map.of(1, -7.0, 0, 8.0))),
                MULTIPLICATION, operation3);
        var operation5 = new Operation(new Polynomial(new HashMap<>(Map.of(1, 9.0, 0, 10.0))),
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

    @Test
    public void testInitializeOperations() {
        var expression = "5+3*-((8*x)/4)";
        var operationsResult = ExpressionUtils.initializeOperations(expression);

        assertNotNull(operationsResult);

        var operation1 = new Operation(new Polynomial(new HashMap<>(Map.of(0, 5d))), NONE, null);
        var operation2 = new Operation(new Polynomial(new HashMap<>(Map.of(0, 3d))), ADDITION, operation1);
        var operation3 = new Operation(new Polynomial(new HashMap<>(Map.of(1, -8d)), new HashMap<>(Map.of(0, 4d))), MULTIPLICATION, operation2);
        assertThat(operationsResult).containsExactlyInAnyOrderElementsOf(List.of(operation1, operation2, operation3));
    }

    @Test
    public void testParseExpression() {
        var expression = "5+3*-((8*x)/4)";
        var operationsResult = ExpressionUtils.initializeOperations(expression);
        var polynomial = ExpressionUtils.collapseOperations(operationsResult);
        assertThat(polynomial.getNumeratorCoefficients())
                .containsExactlyInAnyOrderEntriesOf(Map.of(0, 20d, 1, -24d));
        assertThat(polynomial.getDenominatorCoefficients())
                .containsExactlyInAnyOrderEntriesOf(Map.of(0, 4d));
    }
}