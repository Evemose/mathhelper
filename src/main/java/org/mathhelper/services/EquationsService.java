package org.mathhelper.services;

import lombok.RequiredArgsConstructor;
import org.mathhelper.model.equation.Equation;
import org.mathhelper.repositories.EquationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EquationsService {
    final double precision = Math.pow(10, -9);

    private final EquationRepository equationRepository;

    public Equation save(Equation equation) {
        return equationRepository.save(equation);
    }

    public boolean checkForX(Equation equation, double x) {
        var sumNominator = 0d;
        var sumDenominator = 0d;
        for (var entry : equation.getPolynomialOfEquation().getNumeratorCoefficients().entrySet()) {
            sumNominator += entry.getValue() * Math.pow(x, entry.getKey());
        }
        for (var entry : equation.getPolynomialOfEquation().getDenominatorCoefficients().entrySet()) {
            sumDenominator += entry.getValue() * Math.pow(x, entry.getKey());
        }
        return Math.abs(sumNominator) < precision && Math.abs(sumDenominator) > precision;
    }

    public boolean addSolutionIfSatisfies(Equation equation, double x) {
        if (checkForX(equation, x)) {
            equation.getSolutions().add(x);
            save(equation);
            return true;
        }
        return false;
    }

    public Optional<Equation> findById(Long id) {
        return equationRepository.findById(id);
    }

    public void deleteById(Long id) {
        equationRepository.deleteById(id);
    }

    public Page<Equation> findAllWithSolutionsCountBetween(int min, int max, Pageable pageable) {
        return equationRepository.findWithSolutionsNumberBetween(min, max, pageable);
    }
}
