package org.mathhelper.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.mathhelper.model.validation.equation.EquationConstraint;
import org.mathhelper.utils.expressions.ExpressionUtils;

import java.util.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Equation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotNull
    @EquationConstraint
    @NonNull
    @Setter(AccessLevel.NONE)
    private String equation;

    @ElementCollection
    private List<Double> solutions;

    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    @NonNull
    @NotNull
    private Double coefficient;

    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    @NonNull
    @NotNull
    private Double constant;

    @lombok.Builder(builderClassName = "Builder")
    protected Equation(@NonNull String equation, @NonNull Double coefficient, @NonNull Double constant) {
        this.equation = equation;
        this.coefficient = coefficient;
        this.constant = constant;
    }

    public static class Builder {
        private String equation;
        private Double coefficient;
        private Double constant;

        public Builder equation(String equation) {
            this.equation = equation;
            var parsedEquation = parseEquation(equation);
            this.coefficient = parsedEquation.get(1);
            this.constant = parsedEquation.get(0);
            return this;
        }

        private @NonNull Map<Integer, Double> parseEquation(String equation) {
            equation = equation.replaceAll(" ", "");
            var expression = moveAllToTheLeft(equation);
            return ExpressionUtils.evaluateExpression(expression);
        }


        private String moveAllToTheLeft(String equation) {
            var sides = equation.split("=");
            return sides[0] + "-(" + sides[1] + ")";
        }



        public Equation build() {
            return new Equation(equation, coefficient, constant);
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Equation equation = (Equation) o;
        return getId() != null && Objects.equals(getId(), equation.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}