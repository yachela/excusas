package davinci.edu.ar.excusasSA.model.strategy;

import davinci.edu.ar.excusasSA.model.employee.incharge.InCharge;
import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import davinci.edu.ar.excusasSA.service.EmailSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

class ProductiveTest {

    @InjectMocks
    private Productive productiveStrategy; // La clase a probar
    @Mock
    private InCharge inCharge; // Simula el encargado
    @Mock
    private Excuse excuse; // Simula la excusa
    @Mock
    private EmailSenderService emailSender; // Simula el servicio de correo
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void handlerExcuse_shouldProcessAndSendEmailToCTO() {
        // ACT
        productiveStrategy.handlerExcuse(inCharge, excuse, emailSender);
        // ASSERT
        verify(inCharge, times(1)).processExcuse(excuse, emailSender);
        verify(emailSender, times(1)).sendEmail(
                "CTO@gmail.com",
                "Excusas.S.A.@gmail.com",
                "Excuse Employee",
                "I'll keep you informed of everything"
        );
    }
}