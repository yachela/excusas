package davinci.edu.ar.excusasSA.model.inchargers;

import davinci.edu.ar.excusasSA.model.Employee;
import davinci.edu.ar.excusasSA.model.strategy.Strategy;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

@Entity
public abstract class InCharge extends Employee {

    @Transient
    protected Strategy strategy;

    public InCharge() {
    }

    protected InCharge(String name, String email, Long legajo, Strategy strategy) {
        super(name, email, legajo);
        this.strategy = strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }
}