package davinci.edu.ar.excusasSA.model.excuse.typeExcuse;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@DiscriminatorValue("IMPLAUSIBLE")
public class ImplausibleExcuse extends TypeExcuse {

    @Override
    public boolean isImplausible() { return true; }

    @Override
    protected String getAffair() {
        return "Approved for creativity";
    }

    @Override
    protected String getBody() {
        return "good imagination crack";
    }

    @Override
    public String toString (){ return "ImplausibleExcuse"; }


}
