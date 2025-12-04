package davinci.edu.ar.excusasSA.model.employee;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import davinci.edu.ar.excusasSA.model.employee.incharge.AreaSupervisor;
import davinci.edu.ar.excusasSA.model.employee.incharge.CEO;
import davinci.edu.ar.excusasSA.model.employee.incharge.HumanResourcesManager;
import davinci.edu.ar.excusasSA.model.employee.incharge.Receptionist;
import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import davinci.edu.ar.excusasSA.model.excuse.typeExcuse.TypeExcuse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;

/**
 * Clase Abstracta Base para todos los empleados. Define el comportamiento de
 * generación de excusas.
 */

// --- ANOTACIONES DE JACKSON PARA POLIMORFISMO ---
// 1. Define cómo Jackson debe manejar la herencia (usando la propiedad "@class" para indicar el tipo).
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@class")
// 2. Mapea la propiedad "@class" a los subtipos concretos que puede recibir (MANDATORIO)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Receptionist.class, name = "davinci.edu.ar.excusasSA.model.employee.incharge.Receptionist"),
        @JsonSubTypes.Type(value = CEO.class, name = "davinci.edu.ar.excusasSA.model.employee.incharge.CEO"),
        @JsonSubTypes.Type(value = AreaSupervisor.class, name = "davinci.edu.ar.excusasSA.model.employee.incharge.AreaSupervisor"),
        @JsonSubTypes.Type(value = HumanResourcesManager.class, name = "davinci.edu.ar.excusasSA.model.employee.incharge.HumanResourcesManager")
})
// ------------------------------------------------

@Entity
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "employee_type")
@Table(name = "employees")
public abstract class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    @Column(unique = true, nullable = false)
    private Long legajo;

    protected Employee(String name, String email, Long legajo) {
        this.name = name;
        this.email = email;
        this.legajo = legajo;
    }

    public Excuse generateExcuse( TypeExcuse typeExcuse) {
        Excuse excuse = new Excuse(this, typeExcuse);
        return excuse;
    }
}