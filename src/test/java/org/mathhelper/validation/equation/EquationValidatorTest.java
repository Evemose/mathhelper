package org.mathhelper.validation.equation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;

public class EquationValidatorTest {

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
        Mockito.verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String message = messageCaptor.getValue();
        assertEquals("Parentheses are not balanced", message);

    }

    @ParameterizedTest
    @CsvSource({
            "2+x*2=8",
            "2+x*2=8.0",
            "-2+2*x=8.0",
            "(-2+2)*x=-8.0",
            "(-2*x*(-2))-3=3*x-1",
            "-x+2=-(x)",
    })
    public void testIsValid_PatternCheck_Valid(String equation) {
        var equationValidator = new EquationValidator();
        assertTrue(equationValidator.isValid(equation, context));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2+2(*2)=8.0",
            "2+x*-2=8.0",
            "2+2*?2=8.0",
            "just a = string",
            "-x+2=*(x)",
            "2+=2",
            "x*==4",
            "7+-x=5",
            "xyz+7=+",
            "a==b",
            "=x*2",
            "2/(3+2)=1.23.4",
            "3* 4xy = 12",
            "=5",
            "2+x(2)=6",
    })
    public void testIsValid_PatternCheck_Invalid(String equation) {
        var equationValidator = new EquationValidator();
        assertFalse(equationValidator.isValid(equation, context));
        Mockito.verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String message = messageCaptor.getValue();
        assertEquals("Equation is not valid according to the pattern", message);
    }

}