package davinci.edu.ar.excusasSA.model.excuse.typeExcuse;

import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import davinci.edu.ar.excusasSA.service.EmailSenderService;
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

    public void executeProcess (Excuse excuse, EmailSenderService emailSender){
        emailSender.sendEmail(
                getDestinationEmail(excuse),
                getOriginEmail(),
                getAffair(),
                getBody()
        );
    }

    protected String getDestinationEmail(Excuse excuse){
        return excuse.getEmployee().getEmail();
    }
    protected String getOriginEmail() {
        return "Excusas.S.A.@gmail.com";
    }

    protected abstract String getAffair();
    protected abstract String getBody();
    public abstract String toString();

    public boolean isTrivial() { return false; }
    public boolean isModerate() { return false; }
    public boolean isComplex() { return false; }
    public boolean isImplausible() { return false; }

}
