package org.mathhelper.model.equation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mathhelper.utils.expressions.ExpressionUtils;
import org.mathhelper.utils.expressions.Polynomial;
import org.mockito.InjectMocks;
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

public class EquationMapperTest {

    @InjectMocks
    private EquationMapper mapper;
    @Mock
    private EquationFactory equationFactory;

    private AutoCloseable closable;

    @BeforeEach
    public void setUp() {
        closable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closable.close();
    }

    @Test
    public void testCreateEquationDTOToEquationMapping() {
        var createEquationDTO = new CreateEquationDTO("2+x/5=4");
        Mockito.when(equationFactory.createEquation(anyString()))
                .thenReturn(new Equation("2+x/5=4",
                        new Polynomial(Map.of(1, 1d, 0, -10d), Map.of(0, 5d))));
        var equation = mapper.createEquationDTOToEquation(createEquationDTO);
        assertNotNull(equation);
        assertEquals("2+x/5=4", equation.getEquationString());
        assertThat(equation.getPolynomialOfEquation().getNumeratorCoefficients())
                .containsExactlyInAnyOrderEntriesOf(Map.of(1, 1d, 0, -10d));
        assertThat(equation.getPolynomialOfEquation().getDenominatorCoefficients())
                .containsExactlyInAnyOrderEntriesOf(Map.of(0, 5d));
    }

    @Test
    public void givenEquation_whenConvert_thenCreateEquationDTO() {
        Mockito.when(equationFactory.createEquation(anyString()))
                .thenReturn(new Equation("x^2+2x-1=3",
                        new Polynomial(Map.of(2, 1d, 1, 2d, 0, -4d), Map.of(0, 1d))));
        var equation = equationFactory.createEquation("x^2+2x-1=3");
        var createEquationDTO = mapper.equationToCreateEquationDTO(equation);
        assertEquals(createEquationDTO.equation(), equation.getEquationString());
    }
}