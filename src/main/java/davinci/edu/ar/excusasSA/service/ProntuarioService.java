package davinci.edu.ar.excusasSA.service;

import davinci.edu.ar.excusasSA.event.ProntuarioCreatedEvent;
import davinci.edu.ar.excusasSA.model.prontuario.Prontuario;
import davinci.edu.ar.excusasSA.repository.ProntuarioRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProntuarioService {

    private final ProntuarioRepository prontuarioRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ProntuarioService(ProntuarioRepository prontuarioRepository, ApplicationEventPublisher eventPublisher) {
        this.prontuarioRepository = prontuarioRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<Prontuario> getAllProntuarios() {
        return prontuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Prontuario getProntuarioById(Long id) {
        return prontuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Prontuario no encontrado con ID: " + id));
    }

    @Transactional
    public Prontuario addProntuario(Prontuario prontuario) {
        Prontuario savedProntuario = prontuarioRepository.save(prontuario);
        eventPublisher.publishEvent(new ProntuarioCreatedEvent(this, savedProntuario));
        return savedProntuario;
    }

    @Transactional
    public void resetProntuarios() {
        prontuarioRepository.deleteAll();
    }
}