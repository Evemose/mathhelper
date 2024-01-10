package org.mathhelper.expressions.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionConstraintValidatorTest {

    private final ExpressionValidator expressionConstraintValidator = new ExpressionValidator();
    @Mock
    private ConstraintValidatorContext context;
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
    @ValueSource(strings = {"2+2", "2-2", "2*3", "100/5", "(50+50)*2"})
    public void testValidExpressions_Valid(String expression) {
        assertTrue(expressionConstraintValidator.isValid(expression, context));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2+2+", "2---2", "2*+", "100/5/", "(50+50)*2+"})
    public void testInvalidExpressions_InvalidPattern(String expression) {
        assertFalse(expressionConstraintValidator.isValid(expression, context));
        Mockito.verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        var message = messageCaptor.getValue();
        assertEquals("Expression is not valid: "
                + expression.replaceAll(" ", ""), message);
    }

    @ParameterizedTest
    @ValueSource(strings = {"(2+2)", "(2)+(2)", "(2*(3+1))", "(100/(5+5))", "(50+(50*2))"})
    public void testValidParentheses_Balanced(String expression) {
        assertTrue(expressionConstraintValidator.isValid(expression, context));
    }

    @ParameterizedTest
    @ValueSource(strings = {"((2+2)", "(2+)+2)", "2*(3+1))", "100/(5+5))", "(50+50*2))"})
    public void testInvalidParentheses_Unbalanced(String expression) {
        assertFalse(expressionConstraintValidator.isValid(expression, context));
        Mockito.verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        var message = messageCaptor.getValue();
        assertEquals("Parentheses are not balanced: "
                + expression.replaceAll(" ", ""), message);
    }
}