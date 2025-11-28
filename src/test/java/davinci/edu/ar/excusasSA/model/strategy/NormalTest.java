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
import static org.mockito.Mockito.never;

class NormalTest {

    @InjectMocks
    private Normal normalStrategy;
    @Mock
    private InCharge inCharge;
    @Mock
    private Excuse excuse;
    @Mock
    private EmailSenderService emailSender;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void handlerExcuse_shouldOnlyProcessExcuse() {
        // ACT
        normalStrategy.handlerExcuse(inCharge, excuse, emailSender);
        // ASSERT
        verify(inCharge, times(1)).processExcuse(excuse, emailSender);
        verify(inCharge, never()).nextHandlerExcuse(excuse);
        verify(emailSender, never()).sendEmail(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString()
        );
    }
}