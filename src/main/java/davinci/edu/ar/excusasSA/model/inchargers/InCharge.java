package davinci.edu.ar.excusasSA.model.inchargers;

import davinci.edu.ar.excusasSA.model.Employee;
import davinci.edu.ar.excusasSA.model.Excuse;
import davinci.edu.ar.excusasSA.model.strategy.Strategy;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

@Entity
public abstract class InCharge extends Employee {

    @Transient
    protected InCharge next;

    public InCharge(String name, String email, Long legajo, Strategy strategy) {
    }

    public InCharge() {

    }

    public void setHandler(InCharge next) {
        this.next = next;
    }

    public void handlerExcuse(Excuse excuse) {
        if (canHandleExcuse(excuse)) {
            // this.strategy.handlerExcuse()
            System.out.println("Excusa procesada por " + this.getName());
            excuse.setStatus("PROCESADA");
        } else if (next != null) {
            next.handlerExcuse(excuse);
        } else {
            System.out.println("Nadie pudo procesar la excusa");
            excuse.setStatus("SIN_RESOLUCION");
        }
    }

    protected abstract boolean canHandleExcuse(Excuse excuse);
}