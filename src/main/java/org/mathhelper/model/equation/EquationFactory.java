package org.mathhelper.model.equation;

import lombok.RequiredArgsConstructor;
import org.mathhelper.model.validation.equation.EquationConstraint;
import org.mathhelper.utils.expressions.ExpressionUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
public class EquationFactory {

    private final ExpressionUtils expressionUtils;

    public Equation createEquation(@EquationConstraint String equationString) {
        var split = equationString.split("=");
        var leftPolynomial = expressionUtils.parseExpression(split[0]);
        var rightPolynomial = expressionUtils.parseExpression(split[1]);
        leftPolynomial.subtract(rightPolynomial);
        return new Equation(equationString.strip().replaceAll("\\s+", " "), leftPolynomial);
    }
}
