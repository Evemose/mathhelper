package org.mathhelper.equations.persistence.specifications;

import lombok.NonNull;
import org.mathhelper.equations.dtos.FilterEquationDTO;
import org.mathhelper.equations.persistence.model.Equation;
import org.mathhelper.equations.persistence.model.EquationType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class EquationSpecificationBuilder {

    Specification<Equation> specification = Specification.where(null);

    /**
     * Creates an EquationSpecificationBuilder instance using the provided filter.
     *
     * @param filter The FilterEquationDTO filter object.
     * @return A new EquationSpecificationBuilder instance.
     */
    public static EquationSpecificationBuilder fromFilterDTO(@NonNull FilterEquationDTO filter) {
        return new EquationSpecificationBuilder()
                .solutionsNumberBetween(filter.getMinSolutions(), filter.getMaxSolutions())
                .containsSolutions(filter.getSolutions())
                .anyOfTypes(filter.getEquationTypes())
                .containsFragment(filter.getEquationFragment());
    }

    /**
     * Constructs a new EquationSpecificationBuilder with a specification that filters equations based on the number of solutions between min and max.
     *
     * @param min The minimum number of solutions allowed.
     * @param max The maximum number of solutions allowed.
     * @return This EquationSpecificationBuilder instance.
     */
    public EquationSpecificationBuilder solutionsNumberBetween(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min must be less than or equal to max");
        }
        specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.between(criteriaBuilder.size(root.get("solutions")), min, max));
        return this;
    }

    /**
     * Adds a specification to filter equations that contain any of the given solutions.
     * If solutions is empty, specification will match any solution.
     *
     * @param solutions The list of solutions to search for in the equations.
     * @return This EquationSpecificationBuilder instance.
     */
    public EquationSpecificationBuilder containsSolutions(@NonNull List<Double> solutions) {
        if (!solutions.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) -> solutions.stream()
                    .map(s -> criteriaBuilder.isMember(s, root.get("solutions")))
                    .reduce(criteriaBuilder::or)
                    .orElseThrow());
        }
        return this;
    }

    /**
     * Adds a specification to filter equations that contain any of the given equation types.
     *
     * @param types The list of equation types to search for in the equations.
     * @return This EquationSpecificationBuilder instance.
     */
    public EquationSpecificationBuilder anyOfTypes(@NonNull List<EquationType> types) {
        if (!types.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) -> types.stream()
                    .map(type -> criteriaBuilder.equal(root.get("type"), type))
                    .reduce(criteriaBuilder::or)
                    .orElseThrow());
        }
        return this;
    }

    /**
     * Adds a specification to filter equations that contain the given fragment in the equation string.
     *
     * @param fragment The fragment to search for in the equations.
     * @return This EquationSpecificationBuilder instance.
     */
    public EquationSpecificationBuilder containsFragment(@NonNull String fragment) {
        specification =
                specification.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(root.get("equation"), "%" + fragment + "%"));
        return this;
    }

    public Specification<Equation> build() {
        return specification.and((root, query, criteriaBuilder) -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));
    }

}
