package org.mathhelper.equations.dtos;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mathhelper.equations.dtos.CreateEquationDTO;
import org.mathhelper.equations.dtos.EquationMapper;
import org.mathhelper.equations.persistence.model.Equation;
import org.mathhelper.equations.persistence.model.EquationFactory;
import org.mathhelper.expressions.Polynomial;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
public class EquationMapperTest {

    @Autowired
    private EquationMapper mapper;
    @Autowired
    private EquationFactory equationFactory;

    @Test
    public void testMapCreateDTOToEquation() {
        var createEquationDTO = new CreateEquationDTO("2+x/5=4");
        var equation = mapper.toEquation(createEquationDTO);
        assertNotNull(equation);
        assertEquals("2+x/5=4", equation.getEquationString());
        assertThat(equation.getPolynomial().getNumeratorCoefficients())
                .containsExactlyInAnyOrderEntriesOf(Map.of(1, 1d, 0, -10d));
        assertThat(equation.getPolynomial().getDenominatorCoefficients())
                .containsExactlyInAnyOrderEntriesOf(Map.of(0, 5d));
    }

    @Test
    public void testMapToGetDTO() {
        var equation = equationFactory.createEquation("x+2*x-1=3");
        var createEquationDTO = mapper.toGetDTO(equation);
        assertEquals(createEquationDTO.equation(), equation.getEquationString());
    }
}