package davinci.edu.ar.excusasSA.model.typeExcuse;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "excuse_kind")
@Table(name = "type_excusas")
public abstract class TypeExcuse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId()
    {
        return id;
    }

}
