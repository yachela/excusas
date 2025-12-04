package davinci.edu.ar.excusasSA.listener;

import davinci.edu.ar.excusasSA.event.ProntuarioCreatedEvent;
import davinci.edu.ar.excusasSA.model.prontuario.Prontuario;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CEOProntuarioListener {

    @EventListener
    public void onProntuarioCreated(ProntuarioCreatedEvent event) {
        Prontuario prontuario = event.getProntuario();
        System.out.println("A new prontuario has been added \n" +
                "FROM: "+prontuario.getEmployee().getEmail()+"\n"+
                "Excuse: "+prontuario.getExcuse().getTypeExcuse().toString());
    }
}
