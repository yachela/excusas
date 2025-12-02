package davinci.edu.ar.excusasSA.model.excuse;

import davinci.edu.ar.excusasSA.model.employee.Employee;
import davinci.edu.ar.excusasSA.model.excuse.typeExcuse.TypeExcuse;
import davinci.edu.ar.excusasSA.service.EmailSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExcuseTest {

    @Mock
    private Employee mockEmployee;
    @Mock
    private TypeExcuse mockTypeExcuse;
    @Mock
    private EmailSenderService mockEmailSender;

    private Excuse excuse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void constructor_ShouldInitializeFieldsAndDefaultStatus() {
        excuse = new Excuse(mockEmployee, mockTypeExcuse);
        assertEquals(mockEmployee, excuse.getEmployee(), "El empleado debe ser asignado.");
        assertEquals(mockTypeExcuse, excuse.getTypeExcuse(), "El tipo de excusa debe ser asignado.");
        assertEquals(LocalDate.now(), excuse.getFechaRegistro(), "La fecha de registro debe ser hoy.");
        assertEquals(ExcuseStatus.Pending, excuse.getStatus(), "El estado inicial debe ser Pending.");
        assertNull(excuse.getId(), "El ID debe ser nulo antes de la persistencia.");
    }

    @Test
    void executeProcess_ShouldDelegateToTypeExcuseStrategy() {
        excuse = new Excuse(mockEmployee, mockTypeExcuse);
        excuse.executeProcess(excuse, mockEmailSender);
        verify(mockTypeExcuse, times(1)).executeProcess(excuse, mockEmailSender);
    }

    @Test
    void classificationMethods_ShouldReturnFalseByDefault() {
        excuse = new Excuse(mockEmployee, mockTypeExcuse);
        assertFalse(excuse.isTrivial(), "isTrivial debe ser false por defecto.");
        assertFalse(excuse.isImplausible(), "isImplausible debe ser false por defecto.");
        assertFalse(excuse.isComplex(), "isComplex debe ser false por defecto.");
        assertFalse(excuse.isModerate(), "isModerate debe ser false por defecto.");
    }
}