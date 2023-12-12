package org.mathhelper.repositories;

import org.mathhelper.model.equation.Equation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface EquationRepository extends JpaRepository<Equation, Long>, PagingAndSortingRepository<Equation, Long> {
    @Query("from Equation e where size(e.solutions) >= :minNumber and size(e.solutions) <= :maxNumber")
    Page<Equation> findWithSolutionsNumberBetween(int minNumber, int maxNumber, Pageable pageable);
    List<Equation> findAllBySolutionsContaining(Double solution, Pageable pageable);
}