package davinci.edu.ar.excusasSA.controller;

import davinci.edu.ar.excusasSA.dto.ProntuarioDTO;
import davinci.edu.ar.excusasSA.model.prontuario.Prontuario;
import davinci.edu.ar.excusasSA.service.ProntuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/prontuarios")
public class ProntuarioController {

    private final ProntuarioService prontuarioService;

    @Autowired
    public ProntuarioController(ProntuarioService prontuarioService) {
        this.prontuarioService = prontuarioService;
    }

    // Obtiene la lista completa de todos los prontuarios. Retorna 200 OK.
    @GetMapping
    public List<ProntuarioDTO> getProntuarios() {
        return prontuarioService.getAllProntuarios().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Obtiene un prontuario por su ID. Retorna 200 OK o 404 Not Found.
    @GetMapping("/{id}")
    public ResponseEntity<ProntuarioDTO> getProntuarioById(@PathVariable Long id) {
        try {
            Prontuario prontuario = prontuarioService.getProntuarioById(id);
            return ResponseEntity.ok(mapToDTO(prontuario));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Convierte un objeto Prontuario (modelo) a un ProntuarioDTO.
    private ProntuarioDTO mapToDTO(Prontuario prontuario) {
        ProntuarioDTO dto = new ProntuarioDTO();
        dto.setId(prontuario.getId());
        if (prontuario.getEmployee() != null) {
            dto.setEmployeeId(prontuario.getEmployee().getId());
        }
        if (prontuario.getExcuse() != null) {
            dto.setExcuseId(prontuario.getExcuse().getId());
        }
        return dto;
    }
}