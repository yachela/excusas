package davinci.edu.ar.excusasSA.service;

import davinci.edu.ar.excusasSA.factory.LineInChargeFactory;
import davinci.edu.ar.excusasSA.repository.EmployeeRepository;
import davinci.edu.ar.excusasSA.repository.LineInChargeRepository;
import davinci.edu.ar.excusasSA.repository.ChainLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import davinci.edu.ar.excusasSA.model.lineincharge.LineInCharge;
import davinci.edu.ar.excusasSA.model.lineincharge.ChainLine;

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

    /** Crea una nueva configuración de línea, asegurando la persistencia de ChainLine. */
    public LineInCharge createLineConfig( LineInCharge lineInCharge) {
        ChainLine inputChainLine = lineInCharge.getChainLine();
        ChainLine persistentChainLine = chainLineRepository
                .findByChainIdCode(inputChainLine.getChainIdCode())
                .orElseGet(() -> chainLineRepository.save(new ChainLine(inputChainLine.getChainIdCode())));
        lineInCharge.setChainLine(persistentChainLine);
        LineInCharge savedLineInCharge = lineInChargeRepository.save(lineInCharge);
        String chainIdCode = persistentChainLine.getChainIdCode();
        lineInChargeFactory.rebuildChain(chainIdCode, lineInChargeRepository, employeeRepository);
        return savedLineInCharge;
    }

    /** Actualiza el modo de estrategia para una configuración de línea específica. */
    public LineInCharge updateStrategyMode(Long legajo, String chainIdCode, String newMode) {
        LineInCharge lineInCharge = lineInChargeRepository.findByEmployee_LegajoAndChainLine_ChainIdCode(legajo, chainIdCode)
                .orElseThrow(() -> new NoSuchElementException("Configuración no encontrada para el encargado/línea especificada."));
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