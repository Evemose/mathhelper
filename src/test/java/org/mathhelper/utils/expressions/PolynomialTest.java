package org.mathhelper.utils.expressions;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PolynomialTest {

    @Test
    void addPolynomials_SameDenominator() {
        var coefficients1 = new HashMap<Integer, Double>();
        coefficients1.put(3, 4.d);
        coefficients1.put(2, 3.d);

        var coefficients2 = new HashMap<Integer, Double>();
        coefficients2.put(3, 3.d);
        coefficients2.put(1, 2.d);
        
        var poly1 = new Polynomial(coefficients1);
        var poly2 = new Polynomial(coefficients2);
        
        poly1.add(poly2);
        assertThat(poly1.getNumeratorCoefficients())
                .containsExactlyInAnyOrderEntriesOf(Map.of(3, 7.d, 2, 3.d, 1, 2.d));
    }
    
    @Test
    void addPolynomials_DifferentDenominators() {
        var coefficients1 = new HashMap<Integer, Double>();
        coefficients1.put(3, 4.d);
        coefficients1.put(2, 3.d);

        var coefficientsDenominator1 = new HashMap<Integer, Double>();
        coefficientsDenominator1.put(1, 2.d);

        var coefficients2 = new HashMap<Integer, Double>();
        coefficients2.put(3, 3.d);
        coefficients2.put(1, 2.d);

        var coefficientsDenominator2 = new HashMap<Integer, Double>();
        coefficientsDenominator2.put(1, 3.d);

        var poly1 = new Polynomial(coefficients1, coefficientsDenominator1);
        var poly2 = new Polynomial(coefficients2, coefficientsDenominator2);

        poly1.add(poly2);

        assertThat(poly1.getNumeratorCoefficients())
                .containsExactlyInAnyOrderEntriesOf(Map.of(4, 18.d, 3, 9.d, 2, 4.d));
        assertThat(poly1.getDenominatorCoefficients()).containsExactlyInAnyOrderEntriesOf(Map.of(2, 6.d));
    }

    @Test
    public void testMultiply() {
        var coefs1 = new HashMap<>(Map.of(0, 1.d, 1, 2.d));
        var p1 = new Polynomial(coefs1);
        var coefs2 = new HashMap<>(Map.of(0, 1.d, 1, 3.d));
        p1.multiply(coefs2);
        assertThat(coefs1).containsExactlyInAnyOrderEntriesOf(Map.of(0, 1.d, 1, 5.d, 2, 6.d));
    }

    @Test
    void testSubtract_EqualDenominators() {
        var numeratorCoefficients1 = new HashMap<>(Map.of(1, 2.0, 2, 3.0));
        var denominatorCoefficients1 = new HashMap<>(Map.of(0, 1.0));
        var poly1 = new Polynomial(numeratorCoefficients1, denominatorCoefficients1);

        var numeratorCoefficients2 = new HashMap<>(Map.of(1, 1.0, 2, 2.0));
        var denominatorCoefficients2 = new HashMap<>(Map.of(0, 1.0));
        var poly2 = new Polynomial(numeratorCoefficients2, denominatorCoefficients2);

        poly1.subtract(poly2);

        assertThat(poly1.getNumeratorCoefficients()).containsExactlyInAnyOrderEntriesOf(Map.of(1, 1.0, 2, 1.0));
        assertThat(poly1.getDenominatorCoefficients()).containsExactlyInAnyOrderEntriesOf(Map.of(0, 1.0));
    }

    @Test
    void testSubtract_NotEqualDenominators() {
        var numeratorCoefficients1 = new HashMap<>(Map.of(2, 1.0, 1, 3.0));
        var denominatorCoefficients1 = new HashMap<>(Map.of(0, 4.0));
        var poly1 = new Polynomial(numeratorCoefficients1, denominatorCoefficients1);

        var numeratorCoefficients2 = new HashMap<>(Map.of(1, 1.0, 0, -2.0));
        var denominatorCoefficients2 = new HashMap<>(Map.of(2, 3.0, 0, 5.d));
        var poly2 = new Polynomial(numeratorCoefficients2, denominatorCoefficients2);

        poly1.subtract(poly2);

        assertThat(poly1.getNumeratorCoefficients())
                .containsExactlyInAnyOrderEntriesOf(Map.of(4, 3.d, 3, 9.d, 2, 5.d, 1, 11.d, 0, 8.d));
        assertThat(poly1.getDenominatorCoefficients()).containsExactlyInAnyOrderEntriesOf(Map.of(2, 12.d, 0, 20.d));
    }

    @Test
    void testMultiply_CommonCase() {
        var numeratorCoefficients1 = new HashMap<>(Map.of(2, 1.0, 1, -3.0, 3, -4.0));
        var denominatorCoefficients1 = new HashMap<>(Map.of(0, 4.0));
        var poly1 = new Polynomial(numeratorCoefficients1, denominatorCoefficients1);

        var numeratorCoefficients2 = new HashMap<>(Map.of(1, 1.0, 0, -2.0));
        var denominatorCoefficients2 = new HashMap<>(Map.of(0, 5.d, 2, 3.0));
        var poly2 = new Polynomial(numeratorCoefficients2, denominatorCoefficients2);

        poly1.multiply(poly2);

        assertThat(poly1.getNumeratorCoefficients())
                .containsExactlyInAnyOrderEntriesOf(Map.of(4, -4.d, 3, 9.d, 2, -5.d, 1, 6.d));
        assertThat(poly1.getDenominatorCoefficients())
                .containsExactlyInAnyOrderEntriesOf(Map.of(0, 20.d, 2, 12.d));
    }

    @Test
    public void testMultiply_EmptyCoefficients() {
        var numeratorCoefficients1 = new HashMap<>(Map.of(2, 1.0, 1, -3.0, 3, -4.0));
        var denominatorCoefficients1 = new HashMap<>(Map.of(0, 4.0));
        var poly1 = new Polynomial(numeratorCoefficients1, denominatorCoefficients1);

        var coefs2 = new HashMap<Integer, Double>();
        poly1.multiply(coefs2);
        assertThat(poly1.getNumeratorCoefficients()).containsExactlyInAnyOrderEntriesOf(numeratorCoefficients1);
    }
}