package davinci.edu.ar.excusasSA.model;

import davinci.edu.ar.excusasSA.model.employee.Employee;
import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "prontuarios")
public class Prontuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @OneToOne
    @JoinColumn(name = "excuse_id")
    private Excuse excuse;

    public Prontuario(Employee employee, Excuse excuse) {
        this.employee = employee;
        this.excuse = excuse;
    }
}
