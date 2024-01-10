package org.mathhelper.equations.services;


import org.mathhelper.equations.dtos.FilterEquationDTO;
import org.mathhelper.equations.persistence.model.Equation;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EquationService {
    boolean doesXFit(Equation equation, double x);

    boolean addSolutionIfFits(Equation equation, double x);

    Optional<Equation> findById(Long id);

    Optional<Equation> deleteById(Long id);

    List<Equation> findAll(FilterEquationDTO filter, Pageable pageable);

    Equation save(Equation equation);
}
