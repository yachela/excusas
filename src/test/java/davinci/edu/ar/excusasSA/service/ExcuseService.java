package davinci.edu.ar.excusasSA.service;

import davinci.edu.ar.excusasSA.repository.EmployeeRepository;
import davinci.edu.ar.excusasSA.repository.ExcuseRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExcuseService {

    private final ExcuseRepository excuseRepository;
    private final EmployeeRepository employeeRepository;
    private Incharge chainHead;

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
}
