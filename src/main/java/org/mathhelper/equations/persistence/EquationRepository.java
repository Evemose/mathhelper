package org.mathhelper.equations.persistence;

import lombok.NonNull;
import org.mathhelper.equations.persistence.model.Equation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface EquationRepository extends JpaRepository<Equation, Long>,
        PagingAndSortingRepository<Equation, Long>,
        JpaSpecificationExecutor<Equation> {
}