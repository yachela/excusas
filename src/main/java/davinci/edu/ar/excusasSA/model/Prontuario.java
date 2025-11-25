package davinci.edu.ar.excusasSA.model;

import jakarta.persistence.*;

@Entity
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

    public Prontuario() {}

    public Prontuario(Employee employee, Excuse excuse) {
        this.employee = employee;
        this.excuse = excuse;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Excuse getExcuse() {
        return excuse;
    }

    public void setExcuse(Excuse excuse) {
        this.excuse = excuse;
    }
}
