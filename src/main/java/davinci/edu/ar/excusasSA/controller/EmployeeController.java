package davinci.edu.ar.excusasSA.controller;

import davinci.edu.ar.excusasSA.dto.EmployeeDTO;
import davinci.edu.ar.excusasSA.model.employee.Employee;
import davinci.edu.ar.excusasSA.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // Convierte un objeto Employee (modelo) a un EmployeeDTO.
    private EmployeeDTO mapToDTO(Employee employee) {
        String role = employee.getClass().getSimpleName();

        return new EmployeeDTO(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getLegajo(),
                role
        );
    }

    // Crea un nuevo empleado. Retorna 201 Created.
    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(@Valid @RequestBody EmployeeDTO dto) {
        Employee savedEmployee = employeeService.createEmployee(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(savedEmployee));
    }

    // Obtiene un empleado por su ID. Retorna 200 OK o 404 Not Found.
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(mapToDTO(employee));
    }

    // Obtiene la lista completa de todos los empleados. Retorna 200 OK.
    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        List<EmployeeDTO> employees = employeeService.getAllEmployees().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(employees);
    }
}