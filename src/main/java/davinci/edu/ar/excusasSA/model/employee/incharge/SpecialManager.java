package davinci.edu.ar.excusasSA.model.employee.incharge;

import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import davinci.edu.ar.excusasSA.model.strategy.Normal;
import davinci.edu.ar.excusasSA.model.strategy.Strategy;
import davinci.edu.ar.excusasSA.service.EmailSenderService;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@DiscriminatorValue("SPECIALMANAGER")
public class SpecialManager extends InCharge {

    public SpecialManager(String name, String email, Long legajo) {
        super(name, email, legajo, new Normal());
    }
    @Override
    public void processExcuse(Excuse excuse, EmailSenderService emailSender) {
        emailSender.sendEmail(excuse.getEmployee().getEmail(),this.getEmail(),
                "excuse rejected", "we need hard evidence");
    }

    @Override
    protected boolean canHandleExcuse(Excuse excuse) {
        return true;
    }
}
