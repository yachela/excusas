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

    // Dependencias Simuladas
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
        // Arrange & Act
        excuse = new Excuse(mockEmployee, mockTypeExcuse);
        // Assert
        assertEquals(mockEmployee, excuse.getEmployee(), "El empleado debe ser asignado.");
        assertEquals(mockTypeExcuse, excuse.getTypeExcuse(), "El tipo de excusa debe ser asignado.");
        assertEquals(LocalDate.now(), excuse.getFechaRegistro(), "La fecha de registro debe ser hoy.");
        assertEquals(ExcuseStatus.Pending, excuse.getStatus(), "El estado inicial debe ser Pending.");
        assertNull(excuse.getId(), "El ID debe ser nulo antes de la persistencia.");
    }

    @Test
    void executeProcess_ShouldDelegateToTypeExcuseStrategy() {
        // Arrange
        excuse = new Excuse(mockEmployee, mockTypeExcuse);
        // Act
        excuse.executeProcess(excuse, mockEmailSender);
        // Assert
        verify(mockTypeExcuse, times(1)).executeProcess(excuse, mockEmailSender);
    }

    @Test
    void classificationMethods_ShouldReturnFalseByDefault() {
        // Arrange
        excuse = new Excuse(mockEmployee, mockTypeExcuse);
        // Assert
        assertFalse(excuse.isTrivial(), "isTrivial debe ser false por defecto.");
        assertFalse(excuse.isImplausible(), "isImplausible debe ser false por defecto.");
        assertFalse(excuse.isComplex(), "isComplex debe ser false por defecto.");
        assertFalse(excuse.isModerate(), "isModerate debe ser false por defecto.");
    }
}