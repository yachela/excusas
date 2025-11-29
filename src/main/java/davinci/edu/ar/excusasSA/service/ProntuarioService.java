package davinci.edu.ar.excusasSA.service;

import davinci.edu.ar.excusasSA.event.ProntuarioCreatedEvent;
import davinci.edu.ar.excusasSA.model.prontuario.Prontuario;
import davinci.edu.ar.excusasSA.repository.ProntuarioRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProntuarioService {

    private final ProntuarioRepository prontuarioRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ProntuarioService(ProntuarioRepository prontuarioRepository, ApplicationEventPublisher eventPublisher) {
        this.prontuarioRepository = prontuarioRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Prontuario addProntuario(Prontuario prontuario) {
        Prontuario savedProntuario = prontuarioRepository.save(prontuario);
        eventPublisher.publishEvent(new ProntuarioCreatedEvent(this, savedProntuario));
        return savedProntuario;
    }

    @Transactional(readOnly = true)
    public List<Prontuario> getAllProntuarios() {
        return prontuarioRepository.findAll();
    }

    @Transactional
    public void resetProntuarios() {
        prontuarioRepository.deleteAll();
    }
}