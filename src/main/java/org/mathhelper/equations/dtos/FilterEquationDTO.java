package org.mathhelper.equations.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.mathhelper.equations.persistence.model.EquationType;

import java.util.Collections;
import java.util.List;

/**
 * A data transfer object (DTO) class used for filtering equation objects.
 * It contains various filter parameters to specify the desired characteristics of equations.
 */
@Builder
@Jacksonized
@NoArgsConstructor
@Value
@AllArgsConstructor
public class FilterEquationDTO {
    @Builder.Default
    Integer minSolutions = 0;
    @Builder.Default
    Integer maxSolutions = Integer.MAX_VALUE;
    @Builder.Default
    List<Double> solutions = Collections.emptyList();
    @Builder.Default
    String equationFragment = "";
    @Builder.Default
    List<EquationType> equationTypes = Collections.emptyList();
}
