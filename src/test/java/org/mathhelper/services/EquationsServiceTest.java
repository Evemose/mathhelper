package org.mathhelper.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mathhelper.model.equation.EquationFactory;
import org.mathhelper.repositories.EquationRepository;
import org.mathhelper.utils.expressions.ExpressionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EquationsServiceTest {

    @InjectMocks
    private EquationsService equationsService;
    @InjectMocks
    private EquationFactory equationFactory;
    @Mock
    private ExpressionUtils expressionUtils;
    @Mock
    private EquationRepository equationRepository;

    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        when(expressionUtils.parseExpression(anyString())).thenCallRealMethod();
        when(expressionUtils.collapseOperations(any())).thenCallRealMethod();
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void checkForEqualX_EqualTest() {
        var equation = equationFactory.createEquation("2*x-4=0");
        assertTrue(equationsService.checkForX(equation, 2.0));
    }

    @Test
    void checkForX_NotEqualTest() {
        var equation = equationFactory.createEquation("2*x-4=0");
        assertFalse(equationsService.checkForX(equation, 3.0));
    }

    @Test
    void checkForX_InvalidDenominatorTest() {
        var equation = equationFactory.createEquation("(6*x)/(x-1)=6");
        assertFalse(equationsService.checkForX(equation, 1));
    }
    @Test
    public void addSolutionIfSatisfies_SolutionSatisfiesTest() {
        var equation = equationFactory.createEquation(("x - 4 = -2"));
        double x = 2.0;
        equationsService.addSolutionIfSatisfies(equation, x);
        assertThat(equation.getSolutions()).containsExactlyInAnyOrder(x);
        Mockito.verify(equationRepository).save(equation);
    }

    @Test
    public void addSolutionIfSatisfies_SolutionDoesNotSatisfiesTest() {
        var equation = equationFactory.createEquation(("x - 4 = 2"));
        var x = 4.0;
        equationsService.addSolutionIfSatisfies(equation, x);
        assertTrue(equation.getSolutions().isEmpty());
        verify(equationRepository, times(0)).save(equation);
    }

}