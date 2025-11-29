package davinci.edu.ar.excusasSA.model.employee.incharge;

import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import davinci.edu.ar.excusasSA.model.excuse.ExcuseStatus;
import davinci.edu.ar.excusasSA.model.strategy.Strategy;
import davinci.edu.ar.excusasSA.service.EmailSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ReceptionistTest {

    private Receptionist receptionist;
    @Mock
    private Strategy mockStrategy;
    @Mock
    private EmailSenderService mockEmailSender;
    @Mock
    private Excuse mockExcuse;
    @Mock
    private Handler mockNextHandler;

    private static final String NAME = "Flor Receptionist";
    private static final String EMAIL = "flor@reception.com";
    private static final Long LEGAJO = 100L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        receptionist = new Receptionist(NAME, EMAIL, LEGAJO, mockStrategy);
        receptionist.setEmailSender(mockEmailSender);
        receptionist.setHandler(mockNextHandler);
    }

    @Test
    void canHandleExcuse_shouldReturnTrue_whenExcuseIsTrivial() {
        // ARRANGE
        when(mockExcuse.isTrivial()).thenReturn(true);
        // ACT & ASSERT
        assertTrue(receptionist.canHandleExcuse(mockExcuse),
                "The Receptionist must handle trivial excuses.");
    }

    @Test
    void canHandleExcuse_shouldReturnFalse_whenExcuseIsNotTrivial() {
        // ARRANGE
        when(mockExcuse.isTrivial()).thenReturn(false);
        when(mockExcuse.isComplex()).thenReturn(true);
        // ACT & ASSERT
        assertFalse(receptionist.canHandleExcuse(mockExcuse),
                "The Receptionist must not handle excuses that are not trivial.");
    }

    @Test
    void handlerExcuse_shouldProcess_whenCanHandle() {
        // ARRANGE
        when(mockExcuse.isTrivial()).thenReturn(true);
        // ACT
        receptionist.handlerExcuse(mockExcuse);
        // ASSERT
        verify(mockExcuse, times(1)).setStatus(ExcuseStatus.Processed);
        verify(mockStrategy, times(1)).handlerExcuse(receptionist, mockExcuse, mockEmailSender);
        verify(mockNextHandler, never()).handlerExcuse(mockExcuse);
    }

    @Test
    void handlerExcuse_shouldDelegate_whenCannotHandle() {
        // ARRANGE
        when(mockExcuse.isTrivial()).thenReturn(false);
        // ACT
        receptionist.handlerExcuse(mockExcuse);
        // ASSERT
        verify(mockNextHandler, times(1)).handlerExcuse(mockExcuse);
        verify(mockExcuse, never()).setStatus(any(ExcuseStatus.class));
        verify(mockStrategy, never()).handlerExcuse(any(), any(), any());
    }
}