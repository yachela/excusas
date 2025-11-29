package davinci.edu.ar.excusasSA.service;

import davinci.edu.ar.excusasSA.dto.EmployeeDTO;
import davinci.edu.ar.excusasSA.model.employee.Employee;
import davinci.edu.ar.excusasSA.model.employee.incharge.AreaSupervisor;
import davinci.edu.ar.excusasSA.model.employee.incharge.CEO;
import davinci.edu.ar.excusasSA.model.employee.incharge.HumanResourcesManager;
import davinci.edu.ar.excusasSA.model.employee.incharge.Receptionist;
import davinci.edu.ar.excusasSA.model.strategy.Normal;
import davinci.edu.ar.excusasSA.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public Employee createEmployee(EmployeeDTO dto) {

        // Convertimos el rol a mayúsculas para manejar la comparación una sola vez
        String role = dto.getRole().toUpperCase();
        Employee employee;

        employee = switch (role) {
            case "RECEPTIONIST" -> new Receptionist(dto.getName(), dto.getEmail(), dto.getLegajo(), new Normal());
            case "CEO" -> new CEO(dto.getName(), dto.getEmail(), dto.getLegajo(), new Normal()); // Usa la estrategia apropiada
            case "AREASUPERVISOR" -> new AreaSupervisor(dto.getName(), dto.getEmail(), dto.getLegajo(), new Normal());
            case "HUMANRESOURCESMANAGER" -> new HumanResourcesManager(dto.getName(), dto.getEmail(), dto.getLegajo(), new Normal());
            default -> throw new IllegalArgumentException("Rol no válido o no soportado: " + dto.getRole());
        };

        return employeeRepository.save(employee);
    }

    public Optional<Employee> updateEmployee(Long id, EmployeeDTO dto) {
        return employeeRepository.findById(id).map(employee -> {
            employee.setName(dto.getName());
            employee.setEmail(dto.getEmail());
            employee.setLegajo(dto.getLegajo());
            return employeeRepository.save(employee);
        });
    }

    public boolean deleteEmployee(Long id) {
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            return true;
        }
        return false;
    }
}