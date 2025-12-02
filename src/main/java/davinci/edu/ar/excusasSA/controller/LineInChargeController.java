package davinci.edu.ar.excusasSA.controller;

import davinci.edu.ar.excusasSA.model.lineincharge.LineInCharge;
import davinci.edu.ar.excusasSA.service.LineInChargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/encargados")
public class LineInChargeController {

    private final LineInChargeService lineInChargeService;

    @Autowired
    public LineInChargeController(LineInChargeService configService) {
        this.lineInChargeService = configService;
    }

    // Registra una nueva configuración de línea y encargado. Retorna 200 OK (o 400 si es inválido).
    @PostMapping
    public ResponseEntity<LineInCharge> addLineConfig(@RequestBody LineInCharge lineInCharge) {
        LineInCharge savedConfig = lineInChargeService.createLineConfig(lineInCharge);
        return ResponseEntity.ok(savedConfig);
    }

    // Actualiza el modo de evaluación (estrategia) de un encargado en una línea específica. Retorna 200 OK.
    @PutMapping("/modo")
    public ResponseEntity<LineInCharge> updateMode(
            @RequestParam Long legajo,
            @RequestParam String chainIdCode,
            @RequestParam String newMode) {

        LineInCharge updatedConfig = lineInChargeService.updateStrategyMode(legajo, chainIdCode, newMode);
        return ResponseEntity.ok(updatedConfig);
    }

    // Obtiene la lista completa de todas las configuraciones de encargados de línea. Retorna 200 OK.
    @GetMapping
    public ResponseEntity<List<LineInCharge>> getAllLineInCharge() {
        return ResponseEntity.ok(lineInChargeService.getAllLineInCharge());
    }
}