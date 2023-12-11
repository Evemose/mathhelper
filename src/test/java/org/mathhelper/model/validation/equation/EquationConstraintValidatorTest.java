package org.mathhelper.model.validation.equation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class EquationConstraintValidatorTest {

    @Mock
    private  ConstraintValidatorContext context;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;
    @Captor
    private ArgumentCaptor<String> messageCaptor;

    private AutoCloseable closeable;

    @BeforeEach
    public void setUpBeforeEach() {
        closeable = MockitoAnnotations.openMocks(this);
        Mockito.reset(context);
        Mockito.when(context.buildConstraintViolationWithTemplate(Mockito.anyString()))
                .thenReturn(builder);
    }

    @AfterEach
    public void tearDownAfterEach() throws Exception {
        closeable.close();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "(2+2)*2=8",
            "((2+2)*2)=8"})
    public void testIsValid_ParenthesesValid(String equation) {
        var equationValidator = new EquationValidator();
        assertTrue(equationValidator.isValid(equation, context));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "((2+2)*2=8",
            "(2+2)*2)=8",
            "(2+2)*2=8)",
            "((2+2)*2=8)",
            "((2+2)*2)=8)",
            "((2+2)*2)=8)",
            "((2+2)*2)=)8",
    })
    public void testIsValid_ParenthesesInvalid(String equation) {
        var equationValidator = new EquationValidator();
        assertFalse(equationValidator.isValid(equation, context));
        Mockito.verify(context, Mockito.atLeast(1)).buildConstraintViolationWithTemplate(messageCaptor.capture());
        var message = messageCaptor.getAllValues();
        assertThat(message).containsAnyOf("Parentheses are not balanced: " + equation.split("=")[0],
                "Parentheses are not balanced: " + equation.split("=")[1]);

    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2+x*2=8",
            "2+x*2=8.0",
            "2+x*-2=8.0",
            "-2+2*x=8.0",
            "(-2+2)*x=-8.0",
            "(-2*x*(-2))-3=3*x-1",
            "-x+2=-(x)",
            "7+-x=5",
            "3--4=5",
            "3-(x+2)=5",
    })
    public void testIsValid_PatternCheck_Valid(String equation) {
        var equationValidator = new EquationValidator();
        assertTrue(equationValidator.isValid(equation, context));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2+2(*2)=8.0",
            "2+2*?2=8.0",
            "just a = string",
            "2+=2",
            "x*=4",
            "xyz+7=+",
            "=x*2",
            "3* 4xy = 12",
            "=5",
            "2+x(2)=6",
    })
    public void testIsValid_PatternCheck_LeftSideInvalid(String equation) {
        var equationValidator = new EquationValidator();
        assertFalse(equationValidator.isValid(equation, context));
        Mockito.verify(context, Mockito.times(2)).buildConstraintViolationWithTemplate(messageCaptor.capture());
        var messages = messageCaptor.getAllValues();
        assertThat(messages).containsExactlyInAnyOrder(
                "Left side of equation is invalid: " + equation.replaceAll(" ", "").split("=")[0],
                "Expression is not valid according to the pattern: " + equation.replaceAll(" ", "").split("=")[0]
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "-x+2=*(x)",
            "2/(3+2)=1.23.4",
            "2+2*2=8.0.0",
            "2+2*2=+-*8.0",
    })
    public void testIsValid_PatternCheck_RightSideInvalid(String equation) {
        var equationValidator = new EquationValidator();
        assertFalse(equationValidator.isValid(equation, context));
        Mockito.verify(context, Mockito.times(2)).buildConstraintViolationWithTemplate(messageCaptor.capture());
        var messages = messageCaptor.getAllValues();
        assertThat(messages).containsExactlyInAnyOrder(
                "Right side of equation is invalid: " + equation.replaceAll(" ", "").split("=")[1],
                "Expression is not valid according to the pattern: " + equation.replaceAll(" ", "").split("=")[1]
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2+2*2==8.0",
            "2+2*28"
    })
    public void testIsValid_InvalidEquationSigns(String equation) {
        var equationValidator = new EquationValidator();
        assertFalse(equationValidator.isValid(equation, context));
        Mockito.verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String message = messageCaptor.getValue();
        assertEquals("Equation must have exactly one '=' sign", message);
    }

}