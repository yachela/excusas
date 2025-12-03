package davinci.edu.ar.excusasSA.controller;

import davinci.edu.ar.excusasSA.dto.LineInChargeRequestDTO;
import davinci.edu.ar.excusasSA.model.lineincharge.ChainLine;
import davinci.edu.ar.excusasSA.model.lineincharge.LineInCharge;
import davinci.edu.ar.excusasSA.model.employee.Employee;
import davinci.edu.ar.excusasSA.repository.ChainLineRepository;
import davinci.edu.ar.excusasSA.repository.EmployeeRepository;
import davinci.edu.ar.excusasSA.service.LineInChargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/encargados")
public class LineInChargeController {

    private final LineInChargeService lineInChargeService;
    private final EmployeeRepository employeeRepository;
    private final ChainLineRepository chainLineRepository;

    @Autowired
    public LineInChargeController(LineInChargeService configService, 
                                  EmployeeRepository employeeRepository, 
                                  ChainLineRepository chainLineRepository) {
        this.lineInChargeService = configService;
        this.employeeRepository = employeeRepository;
        this.chainLineRepository = chainLineRepository;
    }

    // AHORA RECIBIMOS EL DTO SIMPLE CON LOS IDs
    @PostMapping
    public ResponseEntity<LineInCharge> addLineConfig(@RequestBody LineInChargeRequestDTO dto) {
        
        // 1. Buscamos las entidades reales en la BD
        ChainLine chain = chainLineRepository.findById(dto.getChainLineId())
                .orElseThrow(() -> new RuntimeException("ChainLine no encontrada con ID: " + dto.getChainLineId()));
        
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee no encontrado con ID: " + dto.getEmployeeId()));

        // 2. Armamos la entidad LineInCharge
        LineInCharge lineInCharge = new LineInCharge();
        lineInCharge.setChainLine(chain);
        lineInCharge.setEmployee(employee);
        lineInCharge.setOrderIndex(dto.getOrderIndex());
        lineInCharge.setStrategyMode(dto.getStrategyMode());

        // 3. Llamamos al servicio
        LineInCharge savedConfig = lineInChargeService.createLineConfig(lineInCharge);
        return ResponseEntity.ok(savedConfig);
    }

    @PutMapping("/modo")
    public ResponseEntity<LineInCharge> updateMode(
            @RequestParam Long legajo,
            @RequestParam String chainIdCode,
            @RequestParam String newMode) {

        LineInCharge updatedConfig = lineInChargeService.updateStrategyMode(legajo, chainIdCode, newMode);
        return ResponseEntity.ok(updatedConfig);
    }

    @GetMapping
    public ResponseEntity<List<LineInCharge>> getAllLineInCharge() {
        return ResponseEntity.ok(lineInChargeService.getAllLineInCharge());
    }
}