package davinci.edu.ar.excusasSA.controller;

import davinci.edu.ar.excusasSA.dto.ProntuarioDTO;
import davinci.edu.ar.excusasSA.model.prontuario.Prontuario;
import davinci.edu.ar.excusasSA.repository.ProntuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/prontuarios")
public class ProntuarioController {

    private final ProntuarioRepository prontuarioRepository;

    @Autowired
    public ProntuarioController(ProntuarioRepository prontuarioRepository) {
        this.prontuarioRepository = prontuarioRepository;
    }

    @GetMapping
    public List<ProntuarioDTO> getProntuarios() {
        return prontuarioRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ProntuarioDTO mapToDTO(Prontuario prontuario) {
        return new ProntuarioDTO(
                prontuario.getEmployee().getName(),
                prontuario.getExcuse().getFechaRegistro().toString(),
                prontuario.getExcuse().getStatus().toString(),
                "Asunto: "
        );
    }
}