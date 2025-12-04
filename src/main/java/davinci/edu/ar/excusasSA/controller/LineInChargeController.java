package davinci.edu.ar.excusasSA.controller;

import davinci.edu.ar.excusasSA.dto.LineInChargeDTO;
import davinci.edu.ar.excusasSA.model.lineincharge.LineInCharge;
import davinci.edu.ar.excusasSA.service.LineInChargeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lineincharge")
public class LineInChargeController {

    private final LineInChargeService lineInChargeService;

    @Autowired
    public LineInChargeController(LineInChargeService configService) {
        this.lineInChargeService = configService;
    }

    // Registra una nueva configuración de línea y encargado, retornando el DTO resultante.
    @PostMapping
    public ResponseEntity<LineInChargeDTO> addLineConfig(@Valid @RequestBody LineInChargeDTO dto) {
        // El servicio crea y guarda la Entidad (LineInCharge)
        LineInCharge savedConfig = lineInChargeService.createLineConfig(dto);

        // Mapeamos la Entidad de vuelta al DTO para la respuesta HTTP
        LineInChargeDTO responseDto = mapToDTO(savedConfig);

        return ResponseEntity.ok(responseDto);
    }

    // Actualiza el modo de evaluación (estrategia) de un encargado en una línea específica. Retorna 200 OK.
    @PutMapping("/modo")
    public ResponseEntity<LineInCharge> updateMode(
            @RequestParam Long legajo,
            @RequestParam String chainIdCode,
            @RequestParam String newMode) {

        // Aquí mantenemos la devolución de la Entidad LineInCharge para simplicidad
        LineInCharge updatedConfig = lineInChargeService.updateStrategyMode(legajo, chainIdCode, newMode);
        return ResponseEntity.ok(updatedConfig);
    }

    // Obtiene la lista completa de todas las configuraciones de encargados de línea, retornando las Entidades.
    @GetMapping
    public ResponseEntity<List<LineInCharge>> getAllLineInCharge() {
        return ResponseEntity.ok(lineInChargeService.getAllLineInCharge());
    }

    /**
     * Helper: Mapea la Entidad LineInCharge a LineInChargeDTO.
     */
    private LineInChargeDTO mapToDTO(LineInCharge entity) {
        LineInChargeDTO dto = new LineInChargeDTO();
        dto.setId(entity.getId());
        // Usamos los IDs de las entidades relacionadas
        if (entity.getEmployee() != null) {
            dto.setEmployeeLegajo(entity.getEmployee().getLegajo());
        }
        if (entity.getChainLine() != null) {
            dto.setChainIdCode(entity.getChainLine().getChainIdCode());
        }
        dto.setOrderIndex(entity.getOrderIndex());
        dto.setStrategyMode(entity.getStrategyMode());
        return dto;
    }
}