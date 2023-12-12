package org.mathhelper.model.equation;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.mathhelper.model.validation.equation.EquationConstraint;
import org.mathhelper.utils.expressions.ExpressionUtils;
import org.mathhelper.utils.expressions.Polynomial;

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

    @Column(nullable = false, unique = true, name="equation")
    @NotNull
    @EquationConstraint
    @NonNull
    @Setter(AccessLevel.NONE)
    private String equationString;

    @ElementCollection
    private final List<Double> solutions = new ArrayList<>();

    @Embedded
    @NonNull
    @NotNull
    @Setter(AccessLevel.NONE)
    private Polynomial polynomialOfEquation;

    Equation(@NonNull String equationString, @NonNull Polynomial polynomial) {
        this.equationString = equationString;
        this.polynomialOfEquation = polynomial;
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