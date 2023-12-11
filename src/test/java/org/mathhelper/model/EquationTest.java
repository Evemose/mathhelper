package org.mathhelper.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mathhelper.model.Equation;
import org.junit.jupiter.api.Assertions;
import org.mathhelper.utils.ExpressionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EquationTest {
    @Test
    void parseEquationTest() {
        var equation = "2x = 10";
        var builder = Equation.builder();
        builder.equation(equation);
        Assertions.assertEquals(2.0, builder.build().getCoefficient());
        Assertions.assertEquals(-10.0, builder.build().getConstant());
    }

    @Test
    void buildTest() {
        String equation = "2x = 10";
        Equation.Builder builder = new Equation.Builder();
        builder.equation(equation);
        Equation outputEquation = builder.build();

        Assertions.assertEquals(equation, outputEquation.getEquation());
        Assertions.assertEquals(2.0, outputEquation.getCoefficient());
        Assertions.assertEquals(-10.0, outputEquation.getConstant());
    }

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
        var op1 = new ExpressionUtils.Operation(Map.of(0, 12.d),
                ExpressionUtils.Operation.Operator.valueOf(operator1), null);
        var op2 = new ExpressionUtils.Operation(Map.of(0, 12.d),
                ExpressionUtils.Operation.Operator.valueOf(operator2), null);
        assertEquals(expected, op1.compareTo(op2));
    }
}