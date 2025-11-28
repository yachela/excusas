package davinci.edu.ar.excusasSA.model.employee.incharge;

import davinci.edu.ar.excusasSA.model.employee.Employee;
import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import davinci.edu.ar.excusasSA.model.excuse.ExcuseStatus;
import davinci.edu.ar.excusasSA.model.strategy.Strategy;
import davinci.edu.ar.excusasSA.service.EmailSenderService;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Entity
@NoArgsConstructor
public abstract class InCharge extends Employee implements Handler {

    protected Strategy strategy;
    @Transient
    protected Handler next;
    protected EmailSenderService emailSender;

    public InCharge(String name, String email, Long legajo, Strategy strategy) {
        super(name, email, legajo);
        this.strategy = strategy;
    }

    public void setHandler(Handler next) {
        this.next = next;
    }

    public void handlerExcuse(Excuse excuse) {
        if (canHandleExcuse(excuse)) {
            // this.strategy.handlerExcuse()
            System.out.println("Excusa procesada por " + this.getName());
            excuse.setStatus(ExcuseStatus.Processed);
        } else if (next != null) {
            next.handlerExcuse(excuse);
        } else {
            System.out.println("Nadie pudo procesar la excusa");
            excuse.setStatus(ExcuseStatus.Unresolved);
        }
    }

    protected abstract boolean canHandleExcuse(Excuse excuse);
}