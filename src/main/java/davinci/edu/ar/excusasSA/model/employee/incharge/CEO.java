package davinci.edu.ar.excusasSA.model.employee.incharge;

import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import davinci.edu.ar.excusasSA.model.excuse.ExcuseStatus;
import davinci.edu.ar.excusasSA.model.prontuario.Prontuario;
import davinci.edu.ar.excusasSA.model.strategy.Strategy;
import davinci.edu.ar.excusasSA.repository.ExcuseRepository;
import davinci.edu.ar.excusasSA.service.EmailSenderService;
import davinci.edu.ar.excusasSA.service.ProntuarioService;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Entity
@Component
@NoArgsConstructor
@DiscriminatorValue("CEO")
public class CEO extends InCharge {

    public CEO(String name, String email, Long legajo, Strategy strategy) {
        super(name, email, legajo, strategy);
    }

    @Override
    protected boolean canHandleExcuse(Excuse excuse) {
        return excuse.isImplausible();
    }

    @Override
    public void processExcuse(Excuse excuse, EmailSenderService emailSender) {
        excuse.setStatus(ExcuseStatus.Accepted);
        excuse.executeProcess(excuse, emailSender);
        excuse.setProcessedByCEO(true);
    }
}