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

class CEOTest {

    private CEO ceo;
    @Mock
    private Strategy mockStrategy;
    @Mock
    private EmailSenderService mockEmailSender;
    @Mock
    private Excuse mockExcuse;
    @Mock
    private Handler mockNextHandler;

    private static final String NAME = "Supreme Boss";
    private static final String EMAIL = "ceo@suprema.com";
    private static final Long LEGAJO = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ceo = new CEO(NAME, EMAIL, LEGAJO, mockStrategy);
        ceo.setEmailSender(mockEmailSender);
        ceo.setHandler(mockNextHandler);
    }

    @Test
    void canHandleExcuse_shouldReturnTrue_whenExcuseIsImplausible() {
        // ARRANGE
        when(mockExcuse.isImplausible()).thenReturn(true);
        // ACT & ASSERT
        assertTrue(ceo.canHandleExcuse(mockExcuse),
                "The CEO must handle implausible excuses.");
    }

    @Test
    void canHandleExcuse_shouldReturnFalse_whenExcuseIsNotImplausible() {
        // ARRANGE
        when(mockExcuse.isImplausible()).thenReturn(false);
        when(mockExcuse.isComplex()).thenReturn(true);
        // ACT & ASSERT
        assertFalse(ceo.canHandleExcuse(mockExcuse),
                "The CEO must not handle excuses that are not implausible.");
    }

    @Test
    void handlerExcuse_shouldProcess_whenCanHandle() {
        // ARRANGE
        when(mockExcuse.isImplausible()).thenReturn(true);
        // ACT
        ceo.handlerExcuse(mockExcuse);
        // ASSERT
        verify(mockExcuse, times(1)).setStatus(ExcuseStatus.Processed);
        verify(mockStrategy, times(1)).handlerExcuse(ceo, mockExcuse, mockEmailSender);
        verify(mockNextHandler, never()).handlerExcuse(mockExcuse);
    }

    @Test
    void handlerExcuse_shouldDelegate_whenCannotHandle() {
        // ARRANGE
        when(mockExcuse.isImplausible()).thenReturn(false);
        // ACT
        ceo.handlerExcuse(mockExcuse);
        // ASSERT
        verify(mockNextHandler, times(1)).handlerExcuse(mockExcuse);
        verify(mockExcuse, never()).setStatus(any(ExcuseStatus.class));
        verify(mockStrategy, never()).handlerExcuse(any(), any(), any());
    }
}