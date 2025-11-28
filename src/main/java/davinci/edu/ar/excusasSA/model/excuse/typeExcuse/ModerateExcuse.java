package davinci.edu.ar.excusasSA.model.excuse.typeExcuse;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MODERATE")
public abstract class ModerateExcuse extends TypeExcuse{
    @Override
    public boolean isModerate() { return true; }
    @Override
    public String toString (){ return "ModerateExcuse"; }
}
