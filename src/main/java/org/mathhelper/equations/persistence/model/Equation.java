package org.mathhelper.equations.persistence.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.mathhelper.equations.validation.EquationConstraint;
import org.mathhelper.expressions.Polynomial;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Equation {
    @ElementCollection(fetch = FetchType.EAGER)
    final List<Double> solutions = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false, unique = true, name = "equation")
    @NotNull
    @EquationConstraint
    @NonNull
    @Setter(AccessLevel.NONE)
    String equationString;
    @Embedded
    @NonNull
    @NotNull
    @Setter(AccessLevel.NONE)
    Polynomial polynomial;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    EquationType type;

    Equation(@NonNull String equationString, @NonNull Polynomial polynomial) {
        this.equationString = equationString;
        this.polynomial = polynomial;
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