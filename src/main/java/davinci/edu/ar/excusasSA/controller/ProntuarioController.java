package davinci.edu.ar.excusasSA.controller;

import davinci.edu.ar.excusasSA.dto.ProntuarioDTO;
import davinci.edu.ar.excusasSA.model.prontuario.Prontuario;
import davinci.edu.ar.excusasSA.repository.ProntuarioRepository;
import davinci.edu.ar.excusasSA.repository.EmployeeRepository;
import davinci.edu.ar.excusasSA.repository.ExcuseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/prontuarios")
public class ProntuarioController {

    private final ProntuarioRepository prontuarioRepository;
    private final EmployeeRepository employeeRepository;
    private final ExcuseRepository excuseRepository;

    @Autowired
    public ProntuarioController(ProntuarioRepository prontuarioRepository,
                                EmployeeRepository employeeRepository,
                                ExcuseRepository excuseRepository) {
        this.prontuarioRepository = prontuarioRepository;
        this.employeeRepository = employeeRepository;
        this.excuseRepository = excuseRepository;
    }

    @GetMapping
    public List<ProntuarioDTO> getProntuarios() {
        return prontuarioRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProntuarioDTO> getProntuarioById(@PathVariable Long id) {
        return prontuarioRepository.findById(id)
                .map(p -> ResponseEntity.ok(mapToDTO(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProntuarioDTO> createProntuario(@RequestBody ProntuarioDTO dto) {
        var employee = employeeRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + dto.getEmployeeId()));

        var excuse = excuseRepository.findById(dto.getExcuseId())
                .orElseThrow(() -> new RuntimeException("Excusa no encontrada con ID: " + dto.getExcuseId()));

        Prontuario prontuario = new Prontuario(employee, excuse);
        Prontuario saved = prontuarioRepository.save(prontuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProntuarioDTO> updateProntuario(@PathVariable Long id, @RequestBody ProntuarioDTO dto) {
        return prontuarioRepository.findById(id)
                .map(prontuario -> {
                    var employee = employeeRepository.findById(dto.getEmployeeId())
                            .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

                    var excuse = excuseRepository.findById(dto.getExcuseId())
                            .orElseThrow(() -> new RuntimeException("Excusa no encontrada"));

                    prontuario.setEmployee(employee);
                    prontuario.setExcuse(excuse);

                    Prontuario updated = prontuarioRepository.save(prontuario);
                    return ResponseEntity.ok(mapToDTO(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProntuario(@PathVariable Long id) {
        if (!prontuarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        prontuarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

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