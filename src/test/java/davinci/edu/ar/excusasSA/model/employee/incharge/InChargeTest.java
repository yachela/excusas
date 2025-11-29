package davinci.edu.ar.excusasSA.model.employee.incharge;

import davinci.edu.ar.excusasSA.model.employee.Employee;
import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import davinci.edu.ar.excusasSA.model.excuse.ExcuseStatus;
import davinci.edu.ar.excusasSA.model.excuse.typeExcuse.TypeExcuse;
import davinci.edu.ar.excusasSA.model.strategy.Strategy;
import davinci.edu.ar.excusasSA.service.EmailSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class InChargeTest {
    @Mock private EmailSenderService mockEmailSender;
    @Mock private Strategy mockStrategy;
    @Mock private Handler mockNextHandler;
    @Mock private Employee mockEmployee;
    @Mock private TypeExcuse mockTypeExcuse;

    private TestInCharge handlerCanHandle;
    private TestInCharge handlerCannotHandle;
    private Excuse spyExcuse;

    private static class TestInCharge extends InCharge {
        private final boolean canHandle;

        public TestInCharge(String name, String email, Long legajo, Strategy strategy, boolean canHandle) {
            super(name, email, legajo, strategy);
            this.canHandle = canHandle;
        }

        @Override
        protected boolean canHandleExcuse(Excuse excuse) {
            return this.canHandle;
        }
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Excuse realExcuse = new Excuse(mockEmployee, mockTypeExcuse);
        spyExcuse = spy(realExcuse);

        handlerCanHandle = spy(new TestInCharge("BossA", "bossA@mail.com", 1L, mockStrategy, true));
        handlerCanHandle.configureService(mockEmailSender);

        handlerCannotHandle = spy(new TestInCharge("BossB", "bossB@mail.com", 2L, mockStrategy, false));
        handlerCannotHandle.configureService(mockEmailSender);
        handlerCannotHandle.setHandler(mockNextHandler);
    }

    @Test
    void handlerExcuse_ShouldExecuteStrategyAndProcess_WhenCanHandleIsTrue() {
        // ARRANGE
        // ACT
        handlerCanHandle.handlerExcuse(spyExcuse);
        // ASSERT
        assertEquals(ExcuseStatus.Processed, spyExcuse.getStatus(), "The excuse status must be Processed.");
        verify(mockStrategy, times(1)).handlerExcuse(handlerCanHandle, spyExcuse, mockEmailSender);
        verify(handlerCanHandle, never()).processExcuse(any(), any());
        verify(spyExcuse, never()).executeProcess(any(), any());
        verify(mockNextHandler, never()).handlerExcuse(any(Excuse.class));
    }

    @Test
    void handlerExcuse_ShouldDelegateToNext_WhenCanHandleIsFalse() {
        // ARRANGE
        // ACT
        handlerCannotHandle.handlerExcuse(spyExcuse);
        // ASSERT
        assertEquals(ExcuseStatus.Pending, spyExcuse.getStatus(), "The excuse status must remain Pending.");
        verify(mockNextHandler, times(1)).handlerExcuse(spyExcuse);
        verify(handlerCannotHandle, never()).processExcuse(any(), any());
        verify(mockStrategy, never()).handlerExcuse(any(), any(), any());
    }

    @Test
    void configureService_ShouldInjectEmailSender() {
        // ARRANGE
        TestInCharge newHandler = new TestInCharge("TestDI", "di@mail.com", 4L, mockStrategy, true);
        // ACT
        newHandler.configureService(mockEmailSender);
        // ASSERT
        assertEquals(mockEmailSender, newHandler.getEmailSender(), "The EmailSender must have been injected.");
    }
}