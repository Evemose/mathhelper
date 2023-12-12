package org.mathhelper.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Assertions;
import org.mathhelper.utils.expressions.Operation;
import org.mathhelper.utils.expressions.Polynomial;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EquationTest {
    @Test
    void parseEquationTest() {
        var equation = "2x = 10";
        var builder = Equation.builder();
        builder.equation(equation);
        Assertions.assertEquals(new Polynomial(Map.of(1, 2d, 0, -10d)), builder.getPolynomialOfEquation());
    }

    @Test
    void buildTest() {
        var equation = "2x = 10";
        var builder = new Equation.Builder();
        builder.equation(equation);
        var outputEquation = builder.build();

        Assertions.assertEquals(equation, outputEquation.getEquation());
        Assertions.assertEquals(new Polynomial(Map.of(1, 2d, 0, -10d)), outputEquation.getPolynomialOfEquation());
    }
}