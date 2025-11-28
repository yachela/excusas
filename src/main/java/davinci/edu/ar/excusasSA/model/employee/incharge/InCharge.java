package davinci.edu.ar.excusasSA.model.employee.incharge;

import davinci.edu.ar.excusasSA.model.employee.Employee;
import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import davinci.edu.ar.excusasSA.model.excuse.ExcuseStatus;
import davinci.edu.ar.excusasSA.model.strategy.Strategy;
import davinci.edu.ar.excusasSA.service.EmailSenderService;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Entity
@Component
@NoArgsConstructor
@Getter
@Setter
public abstract class InCharge extends Employee implements Handler {
    @Transient
    protected Strategy strategy;
    @Transient
    protected Handler next;
    protected EmailSenderService emailSender;

    protected InCharge(String name, String email, Long legajo, Strategy strategy) {
        super(name, email, legajo);
        this.strategy = strategy;
    }

    @Override
    public void setHandler(Handler next) {
        this.next = next;
    }

    @Override
    public void handlerExcuse(Excuse excuse) {
        if (canHandleExcuse(excuse)) {
            excuse.setStatus(ExcuseStatus.Processed);
            this.strategy.handlerExcuse(this, excuse, emailSender);
        } else {
            nextHandlerExcuse(excuse);
        }
    }

    @Autowired
    public void configureService(EmailSenderService emailSender) {
        this.emailSender = emailSender;
    }

    public void nextHandlerExcuse(Excuse excuse) {
        this.next.handlerExcuse(excuse);
    }

    public void processExcuse(Excuse excuse, EmailSenderService emailSender) {
        excuse.executeProcess(excuse, emailSender);
    }

    protected abstract boolean canHandleExcuse(Excuse excuse);
}