package davinci.edu.ar.excusasSA.model.employee.incharge;

import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import davinci.edu.ar.excusasSA.model.employee.Employee;
import davinci.edu.ar.excusasSA.model.prontuario.Prontuario;
import davinci.edu.ar.excusasSA.model.strategy.Strategy;
import davinci.edu.ar.excusasSA.service.EmailSenderService;
import davinci.edu.ar.excusasSA.service.ProntuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.ArgumentCaptor;

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
    private ProntuarioService mockProntuarioService;
    @Mock
    private Employee mockEmployee;

    private static final String NAME = "Supreme Boss";
    private static final String EMAIL = "ceo@suprema.com";
    private static final Long LEGAJO = 1L;

    @BeforeEach
    void setUp() {
        // ARRANGE
        MockitoAnnotations.openMocks(this);
        ceo = new CEO(NAME, EMAIL, LEGAJO, mockStrategy);
        ceo.setProntuarioService(mockProntuarioService);
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
        // ACT & ASSERT
        assertFalse(ceo.canHandleExcuse(mockExcuse),
                "The CEO must not handle excuses that are not implausible.");
    }

    @Test
    void processExcuse_shouldExecuteExcuseAndSaveProntuario() {
        // ARRANGE
        when(mockExcuse.getEmployee()).thenReturn(mockEmployee);
        ArgumentCaptor<Prontuario> prontuarioCaptor = ArgumentCaptor.forClass(Prontuario.class);
        // ACT
        ceo.processExcuse(mockExcuse, mockEmailSender);
        // ASSERT
        verify(mockExcuse, times(1)).executeProcess(mockExcuse, mockEmailSender);
        verify(mockProntuarioService, times(1)).addProntuario(prontuarioCaptor.capture());
        Prontuario createdProntuario = prontuarioCaptor.getValue();
        assertNotNull(createdProntuario, "A Prontuario object must be created.");
        assertSame(mockEmployee, createdProntuario.getEmployee(),
                "The created Prontuario must contain the correct Employee.");
        assertSame(mockExcuse, createdProntuario.getExcuse(),
                "The created Prontuario must contain the processed Excuse.");
    }
}