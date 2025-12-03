package davinci.edu.ar.excusasSA.service;

import davinci.edu.ar.excusasSA.dto.EmployeeDTO;
import davinci.edu.ar.excusasSA.model.employee.Employee;
import davinci.edu.ar.excusasSA.model.employee.EmployeeEnum; // ¡Importamos el Enum!
import davinci.edu.ar.excusasSA.model.strategy.Normal;
import davinci.edu.ar.excusasSA.model.strategy.Strategy;
import davinci.edu.ar.excusasSA.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final Strategy defaultStrategy = new Normal();

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /** Busca un empleado por ID. */
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Employee with ID " + id + " not found."));
    }

    /** Obtiene la lista completa de todos los empleados. */
    public List<Employee> getAllEmployees() {
        return  employeeRepository.findAll();
    }

    /** Crea y persiste un nuevo empleado instanciando la clase correcta según el rol. */
    public Employee createEmployee(EmployeeDTO dto) {
        EmployeeEnum roleEnum;
        try {
            roleEnum = EmployeeEnum.valueOf(dto.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El rol proporcionado '" + dto.getRole() + "' no es un rol de empleado válido.");
        }
        Employee newEmployee = roleEnum.createInstance(
                dto.getName(),
                dto.getEmail(),
                dto.getLegajo(),
                defaultStrategy
        );
        return employeeRepository.save(newEmployee);
    }
}