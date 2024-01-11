package org.mathhelper.expressions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mathhelper.expressions.Operation.Operator.*;

@SpringBootTest
public class ExpressionUtilsTest {
    private final ExpressionUtils expressionUtils = new ExpressionUtils();

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