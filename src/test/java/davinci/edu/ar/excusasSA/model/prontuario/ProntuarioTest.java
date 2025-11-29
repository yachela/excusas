package davinci.edu.ar.excusasSA.model.prontuario;

import davinci.edu.ar.excusasSA.model.employee.Employee;
import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ProntuarioTest {

    private Prontuario prontuario;

    @Mock
    private Employee mockEmployee;
    @Mock
    private Excuse mockExcuse;

    private static final Long TEST_ID = 101L;

    @BeforeEach
    void setUp() {
        // ARRANGE
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void constructorWithArgs_shouldInitializeFieldsCorrectly() {
        // ARRANGE
        prontuario = new Prontuario(mockEmployee, mockExcuse);
        // ACT & ASSERT
        assertEquals(mockEmployee, prontuario.getEmployee(),
                "The Employee must be initialized with the object passed to the constructor.");
        assertEquals(mockExcuse, prontuario.getExcuse(),
                "The Excuse must be initialized with the object passed to the constructor.");
        assertNull(prontuario.getId(), "The ID must be null before persistence.");
    }
}
