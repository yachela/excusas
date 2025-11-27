package davinci.edu.ar.excusasSA.model.inchargers;

import davinci.edu.ar.excusasSA.model.Excuse;
import davinci.edu.ar.excusasSA.model.strategy.Strategy;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("RECEPTIONIST")
public class Receptionist extends InCharge {

    public Receptionist() {
    }

    public Receptionist(String name, String email, Long legajo, Strategy strategy) {
        super(name, email, legajo, strategy);
    }

    @Override
    protected boolean canHandleExcuse(Excuse excuse) {
        return excuse.getTypeExcuse().getClass().getSimpleName().equals("TrivialExcuse");
    }
}