package davinci.edu.ar.excusasSA.service;

import davinci.edu.ar.excusasSA.model.excuse.Excuse;

import davinci.edu.ar.excusasSA.model.employee.incharge.HumanResourcesManager;
import davinci.edu.ar.excusasSA.model.employee.incharge.InCharge;
import davinci.edu.ar.excusasSA.model.employee.incharge.Receptionist;
import davinci.edu.ar.excusasSA.model.strategy.Normal;
import davinci.edu.ar.excusasSA.repository.EmployeeRepository;
import davinci.edu.ar.excusasSA.repository.ExcuseRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExcuseService {

    private final ExcuseRepository excuseRepository;
    private final EmployeeRepository employeeRepository;
    private InCharge chainHead;

    @Autowired
    public ExcuseService(ExcuseRepository excuseRepository, EmployeeRepository employeeRepository) {
        this.excuseRepository = excuseRepository;
        this.employeeRepository = employeeRepository;
    }

    @PostConstruct
    public void initChain() {
        if (employeeRepository.count() == 0) {
            crearEncargadosDefault();
        }
        buildChainInMemory();
    }

    private void crearEncargadosDefault() {
        employeeRepository.save(new Receptionist("Recepcionista", "recep@excusas.com", 1L, new Normal()));
    }

    private void buildChainInMemory() {
        InCharge supervisor = new Receptionist("Recepcionista", "recep@excusas.com", 1L, new Normal());
        InCharge manager = new HumanResourcesManager();

        supervisor.setHandler(manager);

        this.chainHead = supervisor;
    }

    @Transactional
    public void processExcuse(Excuse excuse) {
        if (chainHead != null) {
            chainHead.handlerExcuse(excuse);
        }
        excuseRepository.save(excuse);
    }
}