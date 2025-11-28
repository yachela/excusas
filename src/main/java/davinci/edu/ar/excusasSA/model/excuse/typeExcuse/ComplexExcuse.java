package davinci.edu.ar.excusasSA.model.excuse.typeExcuse;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@DiscriminatorValue("COMPLEX")
public class ComplexExcuse extends TypeExcuse{
    @Override
    public boolean isComplex() {
        return true;
    }

    @Override
    protected String getAffair() {
        return "Excuse Complex";
    }

    @Override
    protected String getBody() {
        return "the license was accepted";
    }

    @Override
    public String toString (){ return "ComplexExcuse"; }
}
