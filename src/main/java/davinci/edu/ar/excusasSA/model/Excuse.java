package davinci.edu.ar.excusasSA.model;

import davinci.edu.ar.excusasSA.model.typeExcuse.TypeExcuse;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "excusas")
public class Excuse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "type_excuse_id")
    private TypeExcuse typeExcuse;

    private LocalDate fechaRegistro;

    private String status;

    public Excuse() {}

    public Excuse(Employee employee, TypeExcuse typeExcuse) {
        this.employee = employee;
        this.typeExcuse = typeExcuse;
        this.fechaRegistro = LocalDate.now();
        this.status = "PENDIENTE";
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
    public TypeExcuse getTypeExcuse() {
        return typeExcuse;
    }

    public void setTypeExcuse(TypeExcuse typeExcuse) {
        this.typeExcuse = typeExcuse;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }
    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

}
