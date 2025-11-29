package davinci.edu.ar.excusasSA.controller;

import davinci.edu.ar.excusasSA.dto.ExcuseDTO;
import davinci.edu.ar.excusasSA.model.employee.Employee;
import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import davinci.edu.ar.excusasSA.model.excuse.ExcuseStatus;
import davinci.edu.ar.excusasSA.model.excuse.typeExcuse.TrivialExcuse;
import davinci.edu.ar.excusasSA.model.excuse.typeExcuse.TypeExcuse;
import davinci.edu.ar.excusasSA.repository.EmployeeRepository;
import davinci.edu.ar.excusasSA.repository.ExcuseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/excusas")
public class ExcuseController {

    private final ExcuseRepository excuseRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public ExcuseController(ExcuseRepository excuseRepository, EmployeeRepository employeeRepository) {
        this.excuseRepository = excuseRepository;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping
    public List<ExcuseDTO> getExcuses() {
        return excuseRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExcuseDTO> getExcuseById(@PathVariable Long id) {
        return excuseRepository.findById(id)
                .map(e -> ResponseEntity.ok(mapToDTO(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ExcuseDTO> createExcuse(@RequestBody ExcuseDTO dto) {
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + dto.getEmployeeId()));

        TypeExcuse typeExcuse;
        if ("TrivialExcuse".equalsIgnoreCase(dto.getTypeExcuseName())) {
            typeExcuse = new TrivialExcuse();
        } else {
            return ResponseEntity.badRequest().build();
        }

        Excuse excuse = new Excuse(employee, typeExcuse);
        Excuse saved = excuseRepository.save(excuse);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExcuseDTO> updateExcuse(@PathVariable Long id, @RequestBody ExcuseDTO dto) {
        return excuseRepository.findById(id)
                .map(excuse -> {
                    if (dto.getStatus() != null) {
                        try {
                            excuse.setStatus(ExcuseStatus.valueOf(dto.getStatus()));
                        } catch (IllegalArgumentException e) {
                            // Si el estado no es válido, se ignora o se podría lanzar error
                        }
                    }

                    if (dto.getEmployeeId() != null) {
                        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
                        excuse.setEmployee(employee);
                    }

                    Excuse updated = excuseRepository.save(excuse);
                    return ResponseEntity.ok(mapToDTO(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExcuse(@PathVariable Long id) {
        if (!excuseRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        excuseRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ExcuseDTO mapToDTO(Excuse excuse) {
        String typeExcuseName = "";
        if (excuse.getTypeExcuse() != null) {
            typeExcuseName = excuse.getTypeExcuse().getClass().getSimpleName();
        }

        return new ExcuseDTO(
                excuse.getId(),
                excuse.getEmployee().getId(),
                typeExcuseName,
                excuse.getFechaRegistro(),
                excuse.getStatus().toString()
        );
    }
}