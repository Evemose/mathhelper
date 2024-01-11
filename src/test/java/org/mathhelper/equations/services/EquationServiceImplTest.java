package org.mathhelper.equations.services;

import org.hibernate.sql.ast.tree.expression.Collation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mathhelper.equations.persistence.EquationRepository;
import org.mathhelper.equations.persistence.model.EquationFactory;
import org.mathhelper.expressions.ExpressionUtils;
import org.mathhelper.expressions.Polynomial;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class EquationServiceImplTest {

    @InjectMocks
    private EquationServiceImpl equationsService;
    private final EquationFactory equationFactory = new EquationFactory(new ExpressionUtils());
    @Mock
    private EquationRepository equationRepository;
    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void checkForEqualX_EqualTest() {
        var equation = equationFactory.createEquation("2*x-4=0");
        assertTrue(equationsService.doesXFit(equation, 2.0));
    }

    @Test
    void checkForX_NotEqualTest() {
        var equation = equationFactory.createEquation("2*x-4=0");
        assertFalse(equationsService.doesXFit(equation, 3.0));
    }

    @Test
    void checkForX_InvalidDenominatorTest() {
        var equation = equationFactory.createEquation("(6*x)/(x-1)=6");
        assertFalse(equationsService.doesXFit(equation, 1));
    }

    @Test
    public void addSolutionIfSatisfies_SolutionSatisfiesTest() {
        var equation = equationFactory.createEquation(("x - 4 = -2"));
        var x = 2.0;
        equationsService.addSolutionIfFits(equation, x);
        assertThat(equation.getSolutions()).containsExactlyInAnyOrder(x);
        Mockito.verify(equationRepository).save(equation);
    }

    @Test
    public void addSolutionIfSatisfies_SolutionDoesNotSatisfiesTest() {
        var equation = equationFactory.createEquation(("x - 4 = 2"));
        var x = 4.0;
        equationsService.addSolutionIfFits(equation, x);
        assertTrue(equation.getSolutions().isEmpty());
        verify(equationRepository, times(0)).save(equation);
    }

}