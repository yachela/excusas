package davinci.edu.ar.excusasSA.factory;

import davinci.edu.ar.excusasSA.model.employee.Employee;
import davinci.edu.ar.excusasSA.model.employee.EmployeeEnum;
import davinci.edu.ar.excusasSA.model.employee.incharge.*;
import davinci.edu.ar.excusasSA.model.lineincharge.LineInCharge;
import davinci.edu.ar.excusasSA.model.strategy.*;
import davinci.edu.ar.excusasSA.repository.LineInChargeRepository;
import davinci.edu.ar.excusasSA.repository.EmployeeRepository;
import davinci.edu.ar.excusasSA.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Locale;
import java.util.Random;
import java.util.ArrayList;

@Component
public class LineInChargeFactory {

    private final Map<String, Handler> availableChains = new HashMap<>();
    private final EmailSenderService emailSenderService;
    private final SpecialManager specialManager;
    private final Random random = new Random();

    @Autowired
    public LineInChargeFactory(LineInChargeRepository lineInChargeRepository,
                               EmployeeRepository employeeRepository,
                               EmailSenderService emailSenderService)
    {
        this.emailSenderService = emailSenderService;

        this.specialManager = new SpecialManager("SPECIALMANAGER", "SpecializedManager@gmail.com", 999L);
        this.specialManager.configureService(emailSenderService);

        loadAllChains(lineInChargeRepository, employeeRepository);
    }

    /** Carga y ensambla todas las cadenas de responsabilidad desde la BD. */
    private void loadAllChains(
            LineInChargeRepository lineInChargeRepository,
            EmployeeRepository employeeRepository
    ) {
        List<String> distinctChainIds = lineInChargeRepository.findAllDistinctChainIds();

        for (String chainId : distinctChainIds) {
            List<LineInCharge> configs = lineInChargeRepository.findByChainLine_ChainIdCodeOrderByOrderIndexAsc(chainId);
            Handler head = buildSpecificChain(configs, employeeRepository);
            availableChains.put(chainId.toUpperCase(Locale.ROOT), head);
        }
    }

    /** Construye la cadena de responsabilidad para un chainId específico. */
    private Handler buildSpecificChain(
            List<LineInCharge> configs, EmployeeRepository employeeRepository
    ) {
        if (configs.isEmpty()) {
            return this.specialManager;
        }
        Handler head = null;
        Handler current = null;
        for (LineInCharge config : configs) {
            Long employeeLegajo = config.getEmployee().getLegajo();
            Employee empData = employeeRepository.findByLegajo(employeeLegajo)
                    .orElseThrow(() -> new NoSuchElementException("Person in charge with file " + employeeLegajo + " not found."));
            InCharge newInCharge = createInChargeInstance(empData, config.getStrategyMode());
            newInCharge.configureService(emailSenderService);
            if (head == null) {
                head = newInCharge;
            } else {
                current.setHandler(newInCharge);
            }
            current = newInCharge;
        }
        if (current != null) {
            current.setHandler(this.specialManager);
        }
        return head;
    }

    /** Crea la instancia del encargado (InCharge) basada en el rol y la estrategia. */
    private InCharge createInChargeInstance(Employee empData, String strategyMode) {
        String roleName = empData.getClass().getSimpleName().toUpperCase(Locale.ROOT);
        EmployeeEnum role = EmployeeEnum.valueOf(roleName);
        Strategy strategy = createStrategy(strategyMode);
        return role.createInstance(empData.getName(), empData.getEmail(), empData.getLegajo(), strategy);
    }

    /** Crea la instancia de la estrategia (Strategy) basada en el modo. */
    private Strategy createStrategy(String mode) {
        String upperMode = mode.toUpperCase(Locale.ROOT);
        StrategyEnum strategyType = StrategyEnum.valueOf(upperMode);
        return strategyType.createStrategyInstance();
    }

    /** Reconstruye y reemplaza una cadena específica tras cambios en la BD. */
    public void rebuildChain(String chainId, LineInChargeRepository lineInChargeRepository, EmployeeRepository employeeRepository) {
        List<LineInCharge> configs = lineInChargeRepository.findByChainLine_ChainIdCodeOrderByOrderIndexAsc(chainId);
        Handler head = buildSpecificChain(configs, employeeRepository);
        availableChains.put(chainId.toUpperCase(Locale.ROOT), head);
    }

    /** Selecciona aleatoriamente uno de los chainId disponibles. */
    public String getRandomChainId() {
        if (availableChains.isEmpty()) {
            throw new IllegalStateException("No chains of supervisors have been loaded at the factory.");
        }
        List<String> chainIds = new ArrayList<>(availableChains.keySet());
        int randomIndex = random.nextInt(chainIds.size());
        return chainIds.get(randomIndex);
    }


    /** Obtiene la cabeza de la cadena de responsabilidad por su ID. */
    public Handler getChainHead(String chainId) {
        Handler chain = availableChains.get(chainId.toUpperCase(Locale.ROOT));
        if (chain == null) {
            throw new IllegalArgumentException("There is no configuration for the manager line: " + chainId);
        }
        return chain;
    }
}