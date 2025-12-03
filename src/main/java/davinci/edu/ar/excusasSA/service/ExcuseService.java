package davinci.edu.ar.excusasSA.service;

import davinci.edu.ar.excusasSA.dto.ExcuseDTO;
import davinci.edu.ar.excusasSA.model.employee.Employee;
import davinci.edu.ar.excusasSA.model.employee.incharge.Handler;
import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import davinci.edu.ar.excusasSA.model.excuse.ExcuseStatus;
import davinci.edu.ar.excusasSA.model.excuse.typeExcuse.TypeExcuse;
import davinci.edu.ar.excusasSA.factory.LineInChargeFactory;
import davinci.edu.ar.excusasSA.factory.TypeExcuseFactory;
import davinci.edu.ar.excusasSA.repository.EmployeeRepository;
import davinci.edu.ar.excusasSA.repository.ExcuseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ExcuseService {

    private final ExcuseRepository excuseRepository;
    private final EmployeeRepository employeeRepository;
    private final TypeExcuseFactory typeExcuseFactory;
    private final LineInChargeFactory lineInChargeFactory;

    /** Inyecta las dependencias necesarias: repositorios y factories. */
    @Autowired
    public ExcuseService(ExcuseRepository excuseRepository,
                         EmployeeRepository employeeRepository,
                         TypeExcuseFactory typeExcuseFactory,
                         LineInChargeFactory lineInChargeFactory) {
        this.excuseRepository = excuseRepository;
        this.employeeRepository = employeeRepository;
        this.typeExcuseFactory = typeExcuseFactory;
        this.lineInChargeFactory = lineInChargeFactory;
    }

    /** Registra una nueva excusa, la procesa por una cadena de encargados ALEATORIA y la persiste. */
    public Excuse registerAndProcess(ExcuseDTO dto) {
        Employee employee = employeeRepository.findByLegajo(dto.getEmployeeLegajo())
                .orElseThrow(() -> new NoSuchElementException("Employee with file " + dto.getEmployeeLegajo() + " not found."));
        String processingChainId = lineInChargeFactory.getRandomChainId();
        TypeExcuse typeExcuse = typeExcuseFactory.createTypeExcuse(dto.getTypeExcuseName());
        Excuse newExcuse = employee.generateExcuse(typeExcuse);
        Handler chainHead = lineInChargeFactory.getChainHead(processingChainId);
        chainHead.handlerExcuse(newExcuse);
        return excuseRepository.save(newExcuse);
    }

    /** Obtiene todas las excusas asociadas a un legajo de empleado. */
    public List<Excuse> getExcusesByEmployeeLegajo(Long legajo) {
        return excuseRepository.findByEmployee_Legajo(legajo);
    }

    /** Obtiene todas las excusas que han sido rechazadas. */
    public List<Excuse> getRejectedExcuses() {
        return excuseRepository.findByStatus(ExcuseStatus.Denied);
    }

    /** Elimina permanentemente las excusas registradas antes de una fecha límite. */
    @Transactional
    public int deleteExcusesBeforeDate(LocalDate fechaLimite) {
        return excuseRepository.deleteByRegisterDateBefore(fechaLimite);
    }

    /** Obtiene excusas aplicando filtros opcionales de rango de fechas y estado. */
    public List<Excuse> getAllExcusesWithFilters(LocalDate dateFrom, LocalDate dateTo, String status) {
        ExcuseStatus statusEnum = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                statusEnum = ExcuseStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid excuse status: " + status);
            }
        }
        return excuseRepository.findFilteredSimple(dateFrom, dateTo, statusEnum);
    }
}