package davinci.edu.ar.excusasSA.model.excuse.typeExcuse;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TRIVIAL")
public class TrivialExcuse extends TypeExcuse{

    @Override
    public boolean isTrivial() { return true; }

    @Override
    protected String getAffair() {
        return "reason for delay";
    }

    @Override
    protected String getBody() {
        return "the license was accepted";
    }

    @Override
    public String toString() {
        return "TrivialExcuse";
    }
}
