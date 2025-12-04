package davinci.edu.ar.excusasSA.service;

import davinci.edu.ar.excusasSA.dto.LineInChargeDTO;
import davinci.edu.ar.excusasSA.factory.LineInChargeFactory;
import davinci.edu.ar.excusasSA.model.employee.Employee;
import davinci.edu.ar.excusasSA.model.lineincharge.ChainLine;
import davinci.edu.ar.excusasSA.model.lineincharge.LineInCharge;
import davinci.edu.ar.excusasSA.repository.ChainLineRepository;
import davinci.edu.ar.excusasSA.repository.EmployeeRepository;
import davinci.edu.ar.excusasSA.repository.LineInChargeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class LineInChargeService {

    private final LineInChargeRepository lineInChargeRepository;
    private final ChainLineRepository chainLineRepository;
    private final LineInChargeFactory lineInChargeFactory;
    private final EmployeeRepository employeeRepository;

    /** Constructor para inyección de dependencias. */
    @Autowired
    public LineInChargeService(
            LineInChargeRepository lineInChargeRepository,
            ChainLineRepository chainLineRepository,
            LineInChargeFactory lineInChargeFactory,
            EmployeeRepository employeeRepository) {
        this.lineInChargeRepository = lineInChargeRepository;
        this.chainLineRepository = chainLineRepository;
        this.lineInChargeFactory = lineInChargeFactory;
        this.employeeRepository = employeeRepository;
    }

    /**
     * Busca o crea la ChainLine por el código, y convierte el DTO a la entidad LineInCharge.
     * Luego guarda la configuración y reconstruye la cadena en la fábrica.
     */
    public LineInCharge createLineConfig(LineInChargeDTO dto) {

        // 1. Obtener el Employee por Legajo
        Employee employee = employeeRepository.findByLegajo(dto.getEmployeeLegajo())
                .orElseThrow(() -> new NoSuchElementException("Employee not found with legajo: " + dto.getEmployeeLegajo()));

        // 2. Obtener o crear la ChainLine
        ChainLine persistentChainLine = chainLineRepository
                .findByChainIdCode(dto.getChainIdCode())
                .orElseGet(() -> chainLineRepository.save(new ChainLine(dto.getChainIdCode())));

        // 3. Mapear DTO a Entidad
        LineInCharge lineInCharge = new LineInCharge();
        lineInCharge.setChainLine(persistentChainLine);
        lineInCharge.setEmployee(employee);
        lineInCharge.setOrderIndex(dto.getOrderIndex());
        lineInCharge.setStrategyMode(dto.getStrategyMode());

        // 4. Guardar la configuración
        LineInCharge savedLineInCharge = lineInChargeRepository.save(lineInCharge);

        // 5. Reconstruir la cadena en la fábrica (uso interno)
        lineInChargeFactory.rebuildChain(
                persistentChainLine.getChainIdCode(),
                lineInChargeRepository,
                employeeRepository
        );

        return savedLineInCharge;
    }

    /** Actualiza el modo de estrategia para una configuración de línea específica. */
    public LineInCharge updateStrategyMode(Long legajo, String chainIdCode, String newMode) {
        LineInCharge lineInCharge = lineInChargeRepository.findByEmployee_LegajoAndChainLine_ChainIdCode(legajo, chainIdCode)
                .orElseThrow(() -> new NoSuchElementException("Configuration not found for the specified in-charge/line"));
        lineInCharge.setStrategyMode(newMode);
        LineInCharge updatedLineInCharge = lineInChargeRepository.save(lineInCharge);
        lineInChargeFactory.rebuildChain(chainIdCode, lineInChargeRepository, employeeRepository);
        return updatedLineInCharge;
    }

    /** Obtiene la lista completa de configuraciones de línea de encargados. */
    public List<LineInCharge> getAllLineInCharge() {
        return lineInChargeRepository.findAll();
    }
}