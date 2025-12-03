package davinci.edu.ar.excusasSA.factory;

import davinci.edu.ar.excusasSA.model.employee.Employee;
import davinci.edu.ar.excusasSA.model.employee.EmployeeEnum;
import davinci.edu.ar.excusasSA.model.employee.incharge.*;
import davinci.edu.ar.excusasSA.model.lineincharge.LineInCharge;
import davinci.edu.ar.excusasSA.model.strategy.*;
import davinci.edu.ar.excusasSA.repository.LineInChargeRepository;
import davinci.edu.ar.excusasSA.repository.EmployeeRepository;
import davinci.edu.ar.excusasSA.service.EmailSenderService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate; // IMPORTANTE: Importamos Hibernate para desempaquetar el proxy
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

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
    
    private final LineInChargeRepository lineInChargeRepository;
    private final EmployeeRepository employeeRepository;
    private final EmailSenderService emailSenderService;
    private final TransactionTemplate transactionTemplate;
    
    private final SpecialManager specialManager;
    private final Random random = new Random();

    @Autowired
    public LineInChargeFactory(LineInChargeRepository lineInChargeRepository,
                               EmployeeRepository employeeRepository,
                               EmailSenderService emailSenderService,
                               TransactionTemplate transactionTemplate) {
        this.lineInChargeRepository = lineInChargeRepository;
        this.employeeRepository = employeeRepository;
        this.emailSenderService = emailSenderService;
        this.transactionTemplate = transactionTemplate;

        this.specialManager = new SpecialManager("SPECIALMANAGER", "SpecializedManager@gmail.com", 999L);
    }

    @PostConstruct
    public void init() {
        this.specialManager.configureService(emailSenderService);
        
        transactionTemplate.execute(status -> {
            loadAllChains();
            return null;
        });
    }

    private void loadAllChains() {
        List<String> distinctChainIds = lineInChargeRepository.findAllDistinctChainIds();

        for (String chainId : distinctChainIds) {
            List<LineInCharge> configs = lineInChargeRepository.findByChainLine_ChainIdCodeOrderByOrderIndexAsc(chainId);
            Handler head = buildSpecificChain(configs);
            availableChains.put(chainId.toUpperCase(Locale.ROOT), head);
        }
    }

    private Handler buildSpecificChain(List<LineInCharge> configs) {
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

    private InCharge createInChargeInstance(Employee empData, String strategyMode) {
        // --- CORRECCIÓN DEFINITIVA ---
        // Hibernate.unproxy fuerza a la base de datos a traer el objeto real.
        // Si empData era un "Employee$Proxy", esto lo convierte en un "Receptionist" (o lo que sea realmente).
        Employee realEmployee = (Employee) Hibernate.unproxy(empData);

        String roleName = realEmployee.getClass().getSimpleName().toUpperCase(Locale.ROOT);
        
        // Mantenemos el filtro de '$' por seguridad extra, aunque unproxy debería haberlo resuelto.
        if (roleName.contains("$")) {
            roleName = roleName.substring(0, roleName.indexOf("$"));
        }

        EmployeeEnum role = EmployeeEnum.valueOf(roleName);
        Strategy strategy = createStrategy(strategyMode);
        return role.createInstance(realEmployee.getName(), realEmployee.getEmail(), realEmployee.getLegajo(), strategy);
    }

    private Strategy createStrategy(String mode) {
        String upperMode = mode.toUpperCase(Locale.ROOT);
        StrategyEnum strategyType = StrategyEnum.valueOf(upperMode);
        return strategyType.createStrategyInstance();
    }

    @Transactional
    public void rebuildChain(String chainId, LineInChargeRepository repo, EmployeeRepository empRepo) {
        List<LineInCharge> configs = repo.findByChainLine_ChainIdCodeOrderByOrderIndexAsc(chainId);
        
        Handler head = buildSpecificChain(configs); 
        availableChains.put(chainId.toUpperCase(Locale.ROOT), head);
    }

    public String getRandomChainId() {
        if (availableChains.isEmpty()) {
            transactionTemplate.execute(status -> {
                loadAllChains();
                return null;
            });
            if (availableChains.isEmpty()) return null;
        }
        List<String> chainIds = new ArrayList<>(availableChains.keySet());
        int randomIndex = random.nextInt(chainIds.size());
        return chainIds.get(randomIndex);
    }

    public Handler getChainHead(String chainId) {
        Handler chain = availableChains.get(chainId.toUpperCase(Locale.ROOT));
        if (chain == null) {
             transactionTemplate.execute(status -> {
                loadAllChains();
                return null;
            });
            chain = availableChains.get(chainId.toUpperCase(Locale.ROOT));
        }
        
        if (chain == null) {
             throw new IllegalArgumentException("There is no configuration for the manager line: " + chainId);
        }
        return chain;
    }
}