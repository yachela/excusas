package davinci.edu.ar.excusasSA.service;

import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import davinci.edu.ar.excusasSA.repository.EmployeeRepository;
import davinci.edu.ar.excusasSA.repository.ExcuseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExcuseServiceTest {

    @Mock
    private ExcuseRepository excuseRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private ExcuseService excuseService;

    @Test
    @DisplayName("Debe procesar una excusa y guardarla en el repositorio")
    void shouldReturnAListOfEmployees() {
        Excuse excuse = new Excuse();

        excuseService.processExcuse(excuse);

        verify(excuseRepository, times(1)).save(any(Excuse.class));
    }
}

