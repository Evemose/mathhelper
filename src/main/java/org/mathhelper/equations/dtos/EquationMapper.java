package org.mathhelper.equations.dtos;

import lombok.AccessLevel;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;
import org.mathhelper.equations.persistence.model.Equation;
import org.mathhelper.equations.persistence.model.EquationFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class EquationMapper {

    @Setter(value = AccessLevel.PROTECTED, onMethod_ = @Autowired)
    protected EquationFactory equationFactory;

    abstract public Equation toEquation(CreateEquationDTO createEquationDTO);

    @Mapping(target = "equation", source = "equationString")
    abstract public GetEquationDTO toGetDTO(Equation equation);

    @ObjectFactory
    protected Equation createEquation(EquationDTO equationDTO) {
        return equationFactory.createEquation(equationDTO.equation());
    }
}
