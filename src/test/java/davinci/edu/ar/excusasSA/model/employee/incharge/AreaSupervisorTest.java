package davinci.edu.ar.excusasSA.model.employee.incharge;

import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import davinci.edu.ar.excusasSA.model.excuse.ExcuseStatus;
import davinci.edu.ar.excusasSA.model.strategy.Strategy;
import davinci.edu.ar.excusasSA.service.EmailSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AreaSupervisorTest {

    private AreaSupervisor areaSupervisor;
    @Mock
    private Strategy mockStrategy;
    @Mock
    private EmailSenderService mockEmailSender;
    @Mock
    private Excuse mockExcuse;
    @Mock
    private Handler mockNextHandler;

    private static final String NAME = "Head Supervisor";
    private static final String EMAIL = "supervisor@area.com";
    private static final Long LEGAJO = 300L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        areaSupervisor = new AreaSupervisor(NAME, EMAIL, LEGAJO, mockStrategy);
        areaSupervisor.setEmailSender(mockEmailSender);
        areaSupervisor.setHandler(mockNextHandler);
    }

    @Test
    void canHandleExcuse_shouldReturnTrue_whenExcuseIsModerate() {
        // ARRANGE
        when(mockExcuse.isModerate()).thenReturn(true);
        // ACT & ASSERT
        assertTrue(areaSupervisor.canHandleExcuse(mockExcuse),
                "The Area Supervisor must handle moderate excuses.");
    }

    @Test
    void canHandleExcuse_shouldReturnFalse_whenExcuseIsNotModerate() {
        // ARRANGE
        when(mockExcuse.isModerate()).thenReturn(false);
        when(mockExcuse.isTrivial()).thenReturn(true);
        // ACT & ASSERT
        assertFalse(areaSupervisor.canHandleExcuse(mockExcuse),
                "The Area Supervisor must not handle excuses that are not moderate.");
    }

    @Test
    void handlerExcuse_shouldProcess_whenCanHandle() {
        // ARRANGE
        when(mockExcuse.isModerate()).thenReturn(true);
        // ACT
        areaSupervisor.handlerExcuse(mockExcuse);
        // ASSERT
        verify(mockExcuse, times(1)).setStatus(ExcuseStatus.Processed);
        verify(mockStrategy, times(1)).handlerExcuse(areaSupervisor, mockExcuse, mockEmailSender);
        verify(mockNextHandler, never()).handlerExcuse(mockExcuse);
    }

    @Test
    void handlerExcuse_shouldDelegate_whenCannotHandle() {
        // ARRANGE
        when(mockExcuse.isModerate()).thenReturn(false);
        // ACT
        areaSupervisor.handlerExcuse(mockExcuse);
        // ASSERT
        verify(mockNextHandler, times(1)).handlerExcuse(mockExcuse);
        verify(mockExcuse, never()).setStatus(any(ExcuseStatus.class));
        verify(mockStrategy, never()).handlerExcuse(any(), any(), any());
    }
}