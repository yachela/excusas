package davinci.edu.ar.excusasSA.model.excuse.typeExcuse;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TRIVIAL")
public class TrivialExcuse extends TypeExcuse{
}
