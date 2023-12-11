package org.mathhelper.utils.expressions;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Polynomial implements Cloneable {

    private Map<Integer, Double> numeratorCoefficients;
    private Map<Integer, Double> denominatorCoefficients;

    public Polynomial() {
        this(new HashMap<>());
    }

    public Polynomial(Map<Integer, Double> numeratorCoefficients) {
        this(numeratorCoefficients, new HashMap<>(Map.of(0, 1.d)));
    }

    public Polynomial(Map<Integer, Double> numeratorCoefficients, Map<Integer, Double> denominatorCoefficients) {
        this.numeratorCoefficients = numeratorCoefficients;
        this.denominatorCoefficients = denominatorCoefficients;
    }

    public void add(Polynomial polynomial) {
        var polynomialCopy = polynomial.clone();
        if (!denominatorCoefficients.equals(polynomial.denominatorCoefficients)) {
            var commonDenominator = new Polynomial(new HashMap<>(this.denominatorCoefficients));
            commonDenominator.multiply(polynomialCopy.denominatorCoefficients);

            multiply(polynomialCopy.denominatorCoefficients);
            polynomialCopy.multiply(denominatorCoefficients);
            denominatorCoefficients = commonDenominator.numeratorCoefficients;
        }
        for (var entry : polynomialCopy.numeratorCoefficients.entrySet()) {
            numeratorCoefficients.merge(entry.getKey(), entry.getValue(), Double::sum);
        }
    }

    public void subtract(Polynomial polynomial) {

    }

    public void multiply(Map<Integer, Double> coefficients) {
        var backupMap = new HashMap<>(numeratorCoefficients);
        numeratorCoefficients.clear();
        for (var entry : backupMap.entrySet()) {
            for (var entry2 : coefficients.entrySet()) {
                numeratorCoefficients.merge(entry.getKey() + entry2.getKey(),
                        entry.getValue() * entry2.getValue(),
                        Double::sum);
            }
        }
    }

    public void multiply(Polynomial polynomial) {

    }


    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public Polynomial clone() {
        return new Polynomial(new HashMap<>(numeratorCoefficients), new HashMap<>(denominatorCoefficients));
    }
}
