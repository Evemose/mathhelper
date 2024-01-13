package org.mathhelper.equations.dtos;

import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.mathhelper.equations.persistence.model.EquationType;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A data transfer object (DTO) class used for filtering equation objects.
 * It contains various filter parameters to specify the desired characteristics of equations.
 */
@Data
@Configurable
@NoArgsConstructor
public class FilterEquationDTO {
    Integer minSolutions = 0;
    Integer maxSolutions = Integer.MAX_VALUE;
    List<Double> solutions = Collections.emptyList();
    String fragment = "";
    List<EquationType> types = Collections.emptyList();
}
