package davinci.edu.ar.excusasSA.model.excuse.typeExcuse.impl;

import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import davinci.edu.ar.excusasSA.model.excuse.typeExcuse.ModerateExcuse;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class PowerOutage extends ModerateExcuse {
    @Override
    protected String getDestinationEmail(Excuse excuse){
        return "EDESUR@mailfake.com.ar";
    }

    @Override
    protected String getAffair() {
        return "question about the blackout";
    }

    @Override
    protected String getBody() {
        return "was there a blackout in such neighborhood?";
    }
}
