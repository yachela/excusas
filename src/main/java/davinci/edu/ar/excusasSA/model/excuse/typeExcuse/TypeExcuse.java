package davinci.edu.ar.excusasSA.model.excuse.typeExcuse;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "excuse_kind")
@Table(name = "type_excusas")
public abstract class TypeExcuse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
