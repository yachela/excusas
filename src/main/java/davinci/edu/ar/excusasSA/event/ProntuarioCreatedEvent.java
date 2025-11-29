package davinci.edu.ar.excusasSA.event;

import davinci.edu.ar.excusasSA.model.prontuario.Prontuario;
import org.springframework.context.ApplicationEvent;

public class ProntuarioCreatedEvent extends ApplicationEvent {

    private final Prontuario prontuario;

    public ProntuarioCreatedEvent(Object source, Prontuario prontuario) {
        super(source);
        this.prontuario = prontuario;
    }

    public Prontuario getProntuario() {
        return prontuario;
    }
}
