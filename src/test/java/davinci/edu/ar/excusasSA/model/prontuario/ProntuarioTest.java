package davinci.edu.ar.excusasSA.model.prontuario;

import davinci.edu.ar.excusasSA.model.employee.Employee;
import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class ProntuarioTest {

    private Prontuario prontuario;

    @Mock
    private Employee mockEmployee;
    @Mock
    private Excuse mockExcuse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void constructorWithArgs_shouldInitializeFieldsCorrectly() {
        prontuario = new Prontuario(mockEmployee, mockExcuse);
        assertEquals(mockEmployee, prontuario.getEmployee(),
                "The Employee must be initialized with the object passed to the constructor.");
        assertEquals(mockExcuse, prontuario.getExcuse(),
                "The Excuse must be initialized with the object passed to the constructor.");
        assertNull(prontuario.getId(), "The ID must be null before persistence.");
    }
}
