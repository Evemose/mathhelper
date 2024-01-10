package org.mathhelper.equations.dtos;

import java.util.List;

public record GetEquationDTO(
        Long id,
        String equation,
        List<Double> solutions) implements EquationDTO {
}
