package davinci.edu.ar.excusasSA.model.employee.incharge;

import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import davinci.edu.ar.excusasSA.model.strategy.Strategy;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
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
}
