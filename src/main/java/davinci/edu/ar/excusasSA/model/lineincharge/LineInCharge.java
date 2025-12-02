package davinci.edu.ar.excusasSA.model.lineincharge;

import davinci.edu.ar.excusasSA.model.employee.Employee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que define la estructura y el orden de una línea de Encargados (Chain of Responsibility).
 * Actúa como la Clase de Asociación, vinculando Chain y Employee, e incluye datos propios
 * (orderIndex y strategyMode).
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "line_in_charge")
public class LineInCharge {

    // ID autonumérico (Clave Primaria).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación ManyToOne: Clave foránea que apunta a la línea (Chain).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chainLine_fk", nullable = false)
    private ChainLine chainLine;

    // Relación ManyToOne: Clave foránea que apunta al empleado (Employee).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_fk", nullable = false)
    private Employee employee;

    // DATOS PROPIOS DE LA ASOCIACIÓN: Posición en la cadena.
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    // DATOS PROPIOS DE LA ASOCIACIÓN: Estrategia específica.
    @Column(name = "strategy_mode", nullable = false)
    private String strategyMode;
}