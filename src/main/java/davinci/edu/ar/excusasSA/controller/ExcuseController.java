package davinci.edu.ar.excusasSA.controller;

import davinci.edu.ar.excusasSA.dto.ExcuseDTO;
import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import davinci.edu.ar.excusasSA.service.ExcuseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/excusas")
public class ExcuseController {

    private final ExcuseService excuseService;

    @Autowired
    public ExcuseController(ExcuseService excuseService) {
        this.excuseService = excuseService;
    }

    // Registra y procesa una nueva excusa. Retorna 201 Created.
    @PostMapping
    public ResponseEntity<ExcuseDTO> registerExcuse(@Valid @RequestBody ExcuseDTO dto) {
        Excuse savedExcuse = excuseService.registerAndProcess(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(savedExcuse));
    }

    // Obtiene una lista de excusas aplicando filtros opcionales (fecha, estado).
    @GetMapping
    public ResponseEntity<List<ExcuseDTO>> getAllExcusesWithFilters(
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateUntil,
            @RequestParam(required = false) String status
    ) {
        List<Excuse> excuses = excuseService.getAllExcusesWithFilters(
                dateFrom,
                dateUntil,
                status
        );
        List<ExcuseDTO> dtoList = excuses.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    // Obtiene todas las excusas asociadas a un número de legajo de empleado específico.
    @GetMapping("/{legajo}")
    public ResponseEntity<List<ExcuseDTO>> getExcusesByEmployeeLegajo(@PathVariable Long legajo) {
        List<ExcuseDTO> dtoList = excuseService.getExcusesByEmployeeLegajo(legajo).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    // Obtiene todas las excusas que se encuentran en estado 'rechazadas'.
    @GetMapping("/rechazadas")
    public ResponseEntity<List<ExcuseDTO>> getRejectedExcuses() {
        List<ExcuseDTO> dtoList = excuseService.getRejectedExcuses().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    // Elimina excusas cuya fecha de registro sea anterior a 'limiteDate'. Requiere el parámetro.
    @DeleteMapping("/eliminar")
    public ResponseEntity<String> deleteExcusesBeforeDate(@RequestParam(required = false) LocalDate limiteDate) {
        if (limiteDate == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "The 'limiteDate' parameter is mandatory for the bulk deletion operation."
            );
        }
        int count = excuseService.deleteExcusesBeforeDate(limiteDate);
        return ResponseEntity.ok("They were successfully removed " + count + " excuses prior to the date " + limiteDate + ".");
    }

    // Convierte un objeto Excuse (modelo) a un ExcuseDTO.
    private ExcuseDTO mapToDTO(Excuse excuse) {
        return new ExcuseDTO(
                excuse.getId(),
                excuse.getStatus().toString(),
                excuse.getRegisterDate(),
                excuse.getEmployee().getLegajo(),
                excuse.getTypeExcuse().toString()
        );
    }
}