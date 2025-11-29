package davinci.edu.ar.excusasSA.model.employee.incharge;

import davinci.edu.ar.excusasSA.model.employee.Employee;
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

class SpecialManagerTest {

    private SpecialManager specialManager;
    @Mock private Strategy mockStrategy;
    @Mock private EmailSenderService mockEmailSender;
    @Mock private Excuse mockExcuse;
    @Mock private Employee mockEmployee;
    @Mock private Handler mockNextHandler;

    private static final String MANAGER_EMAIL = "special@manager.com";
    private static final String EMPLOYEE_EMAIL = "worker@company.com";
    private static final String NAME = "Special Chief";
    private static final Long LEGAJO = 999L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        specialManager = new SpecialManager(NAME, MANAGER_EMAIL, LEGAJO);
        specialManager.setEmailSender(mockEmailSender);
        specialManager.setHandler(mockNextHandler);
        when(mockExcuse.getEmployee()).thenReturn(mockEmployee);
        when(mockEmployee.getEmail()).thenReturn(EMPLOYEE_EMAIL);
    }

    @Test
    void canHandleExcuse_shouldAlwaysReturnTrue() {
        // ARRANGE
        when(mockExcuse.isComplex()).thenReturn(true);
        // ACT & ASSERT
        assertTrue(specialManager.canHandleExcuse(mockExcuse),
                "The Special Manager must handle ANY excuse.");
    }

    @Test
    void handlerExcuse_shouldAlwaysProcess() {
        // ARRANGE
        // ACT
        specialManager.handlerExcuse(mockExcuse);
        // ASSERT
        verify(mockExcuse, times(1)).setStatus(ExcuseStatus.Processed);
        verify(mockStrategy, times(1)).handlerExcuse(specialManager, mockExcuse, mockEmailSender);
        verify(mockNextHandler, never()).handlerExcuse(mockExcuse);
    }

    @Test
    void processExcuse_shouldRejectExcuseAndSendEmail() {
        // ACT
        specialManager.processExcuse(mockExcuse, mockEmailSender);
        // ASSERT
        verify(mockEmailSender, times(1)).sendEmail(
                eq(EMPLOYEE_EMAIL),
                eq(MANAGER_EMAIL),
                eq("excuse rejected"),
                eq("we need hard evidence")
        );
        verify(mockExcuse, never()).executeProcess(any(), any());
    }
}