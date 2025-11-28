package davinci.edu.ar.excusasSA.model.strategy;

import davinci.edu.ar.excusasSA.model.employee.incharge.InCharge;
import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import davinci.edu.ar.excusasSA.service.EmailSenderService;

public interface Strategy {
    void handlerExcuse(InCharge inCharge, Excuse excuse, EmailSenderService emailSender);
}