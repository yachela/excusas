package davinci.edu.ar.excusasSA.model.excuse.typeExcuse.impl;

import davinci.edu.ar.excusasSA.model.excuse.typeExcuse.ModerateExcuse;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class FamilyEmergency extends ModerateExcuse {
    @Override
    protected String getAffair() {
        return "concern to the employee";
    }

    @Override
    protected String getBody() {
        return "is everything okay?";
    }
}
