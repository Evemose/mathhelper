package org.mathhelper.equations.persistence.specifications;

import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mathhelper.equations.persistence.model.Equation;
import org.mathhelper.equations.persistence.model.EquationType;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class EquationSpecificationBuilderTest {
    @Mock
    CriteriaBuilder criteriaBuilder;
    @Mock
    CriteriaQuery<Equation> criteriaQuery;
    @Mock
    Root<Equation> root;

    AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = openMocks(this);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testSolutionsNumberBetween() {
        int min = 1;
        int max = 5;
        var builder = new EquationSpecificationBuilder();

        var specification = builder.solutionsNumberBetween(min, max).build();
        specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        verify(criteriaBuilder).between(criteriaBuilder.size(root.get("solutions")), min, max);
    }

    @Test
    public void testSolutionsNumberBetweenMinGreaterThanMax() {
        var min = 10;
        var max = 5;

        var builder = new EquationSpecificationBuilder();

        assertThrows(IllegalArgumentException.class, () -> builder.solutionsNumberBetween(min, max));
    }

    @Test
    void containsSolutions_Valid() {
        EquationSpecificationBuilder eqBuilder = new EquationSpecificationBuilder();
        @SuppressWarnings("unchecked")
        var mockPath = (Path<Object>) mock(Path.class);
        when(root.get("solutions")).thenReturn(mockPath);
        when(criteriaBuilder.isMember(eq(1.0), any())).thenReturn(Mockito.mock(Predicate.class));
        when(criteriaBuilder.or(any(Predicate.class), any(Predicate.class))).thenReturn(Mockito.mock(Predicate.class));

        var spec = eqBuilder.containsSolutions(Collections.singletonList(1.0)).build();
        spec.toPredicate(root, criteriaQuery, criteriaBuilder);

        verify(criteriaBuilder).isMember(eq(1.0), any());
    }

    /**
     * Test containsSolutions method with solutions list is empty.
     */
    @Test
    void containsSolutions_Empty() {
        EquationSpecificationBuilder eqBuilder = new EquationSpecificationBuilder();

        var spec = eqBuilder.containsSolutions(Collections.emptyList()).build();
        spec.toPredicate(root, criteriaQuery, criteriaBuilder);

        verify(criteriaBuilder, never()).isMember(Optional.ofNullable(any()), any());
    }

    @Test
    public void containsFragmentTest() {
        var fragment = "x";

        var spec = new EquationSpecificationBuilder().containsFragment(fragment).build();
        spec.toPredicate(root, criteriaQuery, criteriaBuilder);

        verify(criteriaBuilder).like(root.get("equation"), "%" + fragment + "%");
    }

    @Test
    public void anyOfTypesTest() {
        var types = List.of(EquationType.LINEAR, EquationType.QUADRATIC);
        @SuppressWarnings("unchecked")
        var mockPath = (Path<Object>) mock(Path.class);
        when(root.get("type")).thenReturn(mockPath);
        when(criteriaBuilder.equal(eq(mockPath), any(EquationType.class))).thenReturn(Mockito.mock(Predicate.class));
        when(criteriaBuilder.or(any(Predicate.class), any(Predicate.class))).thenReturn(Mockito.mock(Predicate.class));

        var spec = new EquationSpecificationBuilder().anyOfTypes(types).build();
        spec.toPredicate(root, criteriaQuery, criteriaBuilder);


        verify(criteriaBuilder).equal(root.get("type"), EquationType.LINEAR);
        verify(criteriaBuilder).equal(root.get("type"), EquationType.QUADRATIC);
        verify(criteriaBuilder, times(2)).equal(eq(root.get("type")), any(EquationType.class));
    }
}
