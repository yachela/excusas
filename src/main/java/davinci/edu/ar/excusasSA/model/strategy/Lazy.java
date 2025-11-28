package davinci.edu.ar.excusasSA.model.strategy;

import davinci.edu.ar.excusasSA.model.employee.incharge.InCharge;
import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import davinci.edu.ar.excusasSA.service.EmailSenderService;

public class Lazy implements Strategy {

    @Override
    public void handlerExcuse(InCharge inCharge, Excuse excuse, EmailSenderService emailSender) {
        System.out.println("I don't want to do it, let someone else do it.");
        inCharge.nextHandlerExcuse(excuse);
    }
}
