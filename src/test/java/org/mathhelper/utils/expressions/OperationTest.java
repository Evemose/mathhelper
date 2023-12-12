package org.mathhelper.utils.expressions;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OperationTest {

    @ParameterizedTest
    @CsvSource({
            "ADDITION, ADDITION, 0",
            "ADDITION, SUBTRACTION, 0",
            "MULTIPLICATION, DIVISION, 0",
            "ADDITION, MULTIPLICATION, -1",
            "SUBTRACTION, DIVISION, -1",
            "MULTIPLICATION, ADDITION, 1",
            "DIVISION, SUBTRACTION, 1",
    })
    public void testCompareToWithDifferentOperators(String operator1, String operator2, int expected) {
        var op1 = new Operation(new Polynomial(new HashMap<>(Map.of(0, 12.d))),
                Operation.Operator.valueOf(operator1), null);
        var op2 = new Operation(new Polynomial(new HashMap<>(Map.of(0, 12.d))),
                Operation.Operator.valueOf(operator2), null);
        assertEquals(expected, op1.compareTo(op2));
    }
}
