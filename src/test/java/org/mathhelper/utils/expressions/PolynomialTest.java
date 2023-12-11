package org.mathhelper.utils.expressions;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PolynomialTest {

    @Test
    void addPolynomialsWithSameDenominator() {
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
    void addPolynomialsWithDifferentDenominator() {
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
}