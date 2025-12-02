package davinci.edu.ar.excusasSA.service;

import davinci.edu.ar.excusasSA.dto.EmployeeDTO;
import davinci.edu.ar.excusasSA.exception.DuplicateResourceException;
import davinci.edu.ar.excusasSA.model.employee.Employee;
import davinci.edu.ar.excusasSA.model.employee.incharge.AreaSupervisor;
import davinci.edu.ar.excusasSA.model.employee.incharge.CEO;
import davinci.edu.ar.excusasSA.model.employee.incharge.HumanResourcesManager;
import davinci.edu.ar.excusasSA.model.employee.incharge.Receptionist;
import davinci.edu.ar.excusasSA.model.strategy.Normal;
import davinci.edu.ar.excusasSA.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con ID: " + id));
    }

    @Transactional
    public Employee createEmployee(EmployeeDTO dto) {
        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("El email " + dto.getEmail() + " ya está registrado.");
        }
        if (employeeRepository.existsByLegajo(dto.getLegajo())) {
            throw new DuplicateResourceException("El legajo " + dto.getLegajo() + " ya está registrado.");
        }

        String role = dto.getRole().toUpperCase();
        Employee employee = switch (role) {
            case "RECEPTIONIST" -> new Receptionist(dto.getName(), dto.getEmail(), dto.getLegajo(), new Normal());
            case "CEO" -> new CEO(dto.getName(), dto.getEmail(), dto.getLegajo(), new Normal());
            case "AREASUPERVISOR" -> new AreaSupervisor(dto.getName(), dto.getEmail(), dto.getLegajo(), new Normal());
            case "HUMANRESOURCESMANAGER" -> new HumanResourcesManager(dto.getName(), dto.getEmail(), dto.getLegajo(), new Normal());
            default -> throw new IllegalArgumentException("Rol no válido: " + dto.getRole());
        };

        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee updateEmployee(Long id, EmployeeDTO dto) {
        Employee employee = getEmployeeById(id);

        if (!employee.getEmail().equals(dto.getEmail()) && employeeRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("El email " + dto.getEmail() + " ya está en uso por otro empleado.");
        }

        employee.setName(dto.getName());
        employee.setEmail(dto.getEmail());
        employee.setLegajo(dto.getLegajo());

        return employeeRepository.save(employee);
    }

    public boolean deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new EntityNotFoundException("No se puede eliminar. Empleado no encontrado con ID: " + id);
        }
        employeeRepository.deleteById(id);
        return true;
    }
}