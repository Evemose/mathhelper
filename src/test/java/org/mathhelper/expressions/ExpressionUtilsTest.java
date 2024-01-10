package org.mathhelper.expressions;

import org.junit.jupiter.api.Test;
import org.mathhelper.expressions.ExpressionUtils;
import org.mathhelper.expressions.Operation;
import org.mathhelper.expressions.Polynomial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mathhelper.expressions.Operation.Operator.*;

@SpringBootTest
public class ExpressionUtilsTest {

    @Autowired
    private ExpressionUtils expressionUtils;

    @Test
    public void collapseOperationsTest_NoOperations() {
        var operation = new Operation(new Polynomial(new HashMap<>(Map.of(1, 2.0))), NONE, null);

        var operations = new PriorityQueue<Operation>();
        operations.add(operation);

        var result = expressionUtils.collapseOperations(operations);

        assertEquals(2.0, result.getNumeratorCoefficients().get(1));
    }

    @Test
    public void testCollapse_ComplexExpression() {
        var resultantOperation = expressionUtils.collapseOperations(getComplexExpression());
        assertNotNull(resultantOperation);
        assertThat(resultantOperation.getNumeratorCoefficients()).containsExactlyInAnyOrderEntriesOf(
               Map.of(0, -158.3, 1, -155.5, 2, 502d, 3, -224d));
        assertThat(resultantOperation.getDenominatorCoefficients()).containsExactlyInAnyOrderEntriesOf(
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
    public void testParseExpression() {
        var expression = "5+3*-((8*x)/4)";
        var polynomial = expressionUtils.parseExpression(expression);
        assertThat(polynomial.getNumeratorCoefficients())
                .containsExactlyInAnyOrderEntriesOf(Map.of(0, 20d, 1, -24d));
        assertThat(polynomial.getDenominatorCoefficients())
                .containsExactlyInAnyOrderEntriesOf(Map.of(0, 4d));
    }
}