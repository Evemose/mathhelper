package org.mathhelper.model.equation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EquationMapper {
    protected final EquationFactory equationFactory;

    public Equation createEquationDTOToEquation(CreateEquationDTO createEquationDTO) {
        if (createEquationDTO == null) {
            return null;
        }
        return equationFactory.createEquation(createEquationDTO.equation());
    }


    public CreateEquationDTO equationToCreateEquationDTO(Equation equation) {
        if (equation == null) {
            return null;
        }
        return new CreateEquationDTO(equation.getEquationString());
    }
}
