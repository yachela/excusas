package davinci.edu.ar.excusasSA.model.strategy;

import davinci.edu.ar.excusasSA.model.employee.incharge.InCharge;
import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import davinci.edu.ar.excusasSA.service.EmailSenderService;

public class Productive implements Strategy {

    @Override
    public void handlerExcuse(InCharge inCharge, Excuse excuse, EmailSenderService emailSender) {
        inCharge.processExcuse(excuse, emailSender);
        emailSender.sendEmail("CTO@gmail.com","Excusas.S.A.@gmail.com",
                "Excuse Employee", "I'll keep you informed of everything");
    }
}
