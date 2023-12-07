package org.mathhelper;

import org.mathhelper.model.Equation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EquationRepository extends JpaRepository<Equation, Long> {
    @Query("from Equation e where size(e.solutions) >= :number")
    List<Equation> findWithMoreOrEqualSolutionsThan(@Param("number") int solutionsNumber);

    @Query("from Equation e where size(e.solutions) = :number")
    List<Equation> findWithExactlySolutionsNumber(@Param("number") int solutionsNumber);

    @Query("from Equation e where size(e.solutions) <= :number")
    List<Equation> findWithLessOrEqualSolutionsThan(@Param("number") int solutionsNumber);


    List<Equation> findAllBySolutionsContaining(Double solution);
}