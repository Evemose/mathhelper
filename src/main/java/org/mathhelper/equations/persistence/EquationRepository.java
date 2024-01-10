package org.mathhelper.equations.persistence;

import org.mathhelper.equations.persistence.model.Equation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface EquationRepository extends JpaRepository<Equation, Long>,
        PagingAndSortingRepository<Equation, Long>,
        JpaSpecificationExecutor<Equation> {
}