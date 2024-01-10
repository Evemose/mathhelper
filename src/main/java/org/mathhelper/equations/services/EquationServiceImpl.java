package org.mathhelper.equations.services;

import lombok.RequiredArgsConstructor;
import org.mathhelper.equations.dtos.FilterEquationDTO;
import org.mathhelper.equations.persistence.EquationRepository;
import org.mathhelper.equations.persistence.model.Equation;
import org.mathhelper.equations.persistence.specifications.EquationSpecificationBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EquationServiceImpl implements EquationService {
    final double precision = Math.pow(10, -9);
    final EquationRepository equationRepository;

    @Override
    public Equation save(Equation equation) {
        return equationRepository.save(equation);
    }

    @Override
    public boolean doesXFit(Equation equation, double x) {
        var sumNominator = 0d;
        var sumDenominator = 0d;
        for (var entry : equation.getPolynomial().getNumeratorCoefficients().entrySet()) {
            sumNominator += entry.getValue() * Math.pow(x, entry.getKey());
        }
        for (var entry : equation.getPolynomial().getDenominatorCoefficients().entrySet()) {
            sumDenominator += entry.getValue() * Math.pow(x, entry.getKey());
        }
        return Math.abs(sumNominator) < precision && Math.abs(sumDenominator) > precision;
    }

    @Override
    public boolean addSolutionIfFits(Equation equation, double x) {
        if (doesXFit(equation, x)) {
            equation.getSolutions().add(x);
            save(equation);
            return true;
        }
        return false;
    }

    @Override
    public Optional<Equation> findById(Long id) {
        return equationRepository.findById(id);
    }

    @Override
    public Optional<Equation> deleteById(Long id) {
        var equation = equationRepository.findById(id);
        equation.ifPresent(equationRepository::delete);
        return equation;
    }

    @Override
    public List<Equation> findAll(FilterEquationDTO filter, Pageable pageable) {
        return equationRepository.findAll(EquationSpecificationBuilder.fromFilterDTO(filter).build(), pageable).getContent();
    }
}
