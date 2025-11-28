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

class LazyTest {

    @InjectMocks
    private Lazy lazyStrategy;
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
    void handlerExcuse_shouldDelegateToNextHandlerAndNotProcess() {
        // ACT
        lazyStrategy.handlerExcuse(inCharge, excuse, emailSender);
        // ASSERT
        verify(inCharge, times(1)).nextHandlerExcuse(excuse);
        verify(inCharge, never()).processExcuse(excuse, emailSender);
        verify(emailSender, never()).sendEmail(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString()
        );
    }
}