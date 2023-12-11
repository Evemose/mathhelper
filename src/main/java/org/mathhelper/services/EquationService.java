package org.mathhelper.services;

import lombok.RequiredArgsConstructor;
import org.mathhelper.model.Equation;
import org.mathhelper.repositories.EquationRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EquationService {

    private final EquationRepository equationRepository;

    public void save(Equation equation) {
        equationRepository.save(equation);
    }

    public boolean checkForX(Equation equation, double x) {
        return equation.getCoefficient() * x + equation.getConstant() == 0;
    }
}
