package org.mathhelper.utils.expressions;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;

@Data
@Builder
@Embeddable
public class Polynomial implements Cloneable {

    @ElementCollection
    @NonNull
    private Map<Integer, Double> numeratorCoefficients;
    @ElementCollection
    @NonNull
    private Map<Integer, Double> denominatorCoefficients;

    public Polynomial() {
        this(new HashMap<>());
    }

    public Polynomial(@NonNull Map<Integer, Double> numeratorCoefficients) {
        this(numeratorCoefficients, new HashMap<>(Map.of(0, 1.d)));
    }

    public Polynomial(@NonNull Map<Integer, Double> numeratorCoefficients, @NonNull Map<Integer, Double> denominatorCoefficients) {
        setNumeratorCoefficients(numeratorCoefficients);
        setDenominatorCoefficients(denominatorCoefficients);
    }

    public void add(@NonNull Polynomial polynomial) {
        applyBinaryOperator(polynomial, Double::sum);
    }

    public void subtract(@NonNull Polynomial polynomial) {
        applyBinaryOperator(polynomial, (a, b) -> a - b);
    }

    private void applyBinaryOperator(Polynomial polynomial, BinaryOperator<Double> binaryOperator) {
        var polynomialCopy = polynomial.clone();
        if (!denominatorCoefficients.equals(polynomial.denominatorCoefficients)) {
            var commonDenominator = new Polynomial(new HashMap<>(this.denominatorCoefficients));
            commonDenominator.multiply(polynomialCopy.denominatorCoefficients);

            multiply(polynomialCopy.denominatorCoefficients);
            polynomialCopy.multiply(denominatorCoefficients);
            denominatorCoefficients = commonDenominator.numeratorCoefficients;
        }
        for (var entry : polynomialCopy.numeratorCoefficients.entrySet()) {
            if (numeratorCoefficients.containsKey(entry.getKey())) {
                numeratorCoefficients.merge(entry.getKey(), entry.getValue(), binaryOperator);
            } else {
                numeratorCoefficients.put(entry.getKey(), binaryOperator.apply(0.d, entry.getValue()));
            }
        }
    }

    public void multiply(@NonNull Map<Integer, Double> coefficients) {
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

    public void multiply(@NonNull Polynomial polynomial) {
        multiply(polynomial.numeratorCoefficients);
        multiplyDenominator(polynomial);
    }

    public void multiply(double coefficient) {
        for (var entry : numeratorCoefficients.entrySet()) {
            entry.setValue(entry.getValue() * coefficient);
        }
    }

    public void setDenominatorCoefficients(@NonNull Map<Integer, Double> denominatorCoefficients) {
        if (denominatorCoefficients.isEmpty()) {
            throw new IllegalArgumentException("Denominator cannot be empty");
        }
        if (denominatorCoefficients.get(0) == 0 && denominatorCoefficients.size() == 1) {
            throw new IllegalArgumentException("Denominator cant be zero");
        }
        this.denominatorCoefficients = denominatorCoefficients;
    }

    public void setNumeratorCoefficients(@NonNull Map<Integer, Double> numeratorCoefficients) {
        if (numeratorCoefficients.isEmpty()) {
            throw new IllegalArgumentException("Numerator cannot be empty");
        }
        this.numeratorCoefficients = numeratorCoefficients;
    }

    public void divide(@NonNull Polynomial polynomial) {
        if (polynomial.numeratorCoefficients.isEmpty() ||
                polynomial.numeratorCoefficients.get(0) == 0 && polynomial.numeratorCoefficients.size() == 1) {
            throw new IllegalArgumentException("Cannot divide by zero");
        }
        multiply(new Polynomial(polynomial.denominatorCoefficients, polynomial.numeratorCoefficients));
    }

    private void multiplyDenominator(@NonNull Polynomial polynomial) {
        var denominatorPolynomial = new Polynomial(new HashMap<>(denominatorCoefficients));
        denominatorPolynomial.multiply(polynomial.denominatorCoefficients);
        denominatorCoefficients = denominatorPolynomial.numeratorCoefficients;
    }


    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public Polynomial clone() {
        return new Polynomial(new HashMap<>(numeratorCoefficients), new HashMap<>(denominatorCoefficients));
    }
}
