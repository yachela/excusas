package davinci.edu.ar.excusasSA.model.excuse;

import davinci.edu.ar.excusasSA.model.employee.Employee;
import davinci.edu.ar.excusasSA.model.excuse.typeExcuse.TypeExcuse;
import davinci.edu.ar.excusasSA.service.EmailSenderService;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "excusas")
public class Excuse implements IExcuse {

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

    private ExcuseStatus status;

    public Excuse(Employee employee, TypeExcuse typeExcuse) {
        this.employee = employee;
        this.typeExcuse = typeExcuse;
        this.fechaRegistro = LocalDate.now();
        this.status = ExcuseStatus.Pending;
    }

    public void executeProcess(Excuse excuse , EmailSenderService emailSender) {
        this.typeExcuse.executeProcess(excuse , emailSender);
    }
    @Override
    public boolean isTrivial() {
        return false;
    }

    @Override
    public boolean isImplausible() {
        return false;
    }

    @Override
    public boolean isComplex() {
        return false;
    }

    @Override
    public boolean isModerate() {
        return false;
    }
}
