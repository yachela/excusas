package davinci.edu.ar.excusasSA.model.employee;

import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import davinci.edu.ar.excusasSA.model.excuse.ExcuseStatus;
import davinci.edu.ar.excusasSA.model.excuse.typeExcuse.TrivialExcuse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EmployeeTest {

    private static class ConcreteEmployee extends Employee {
        public ConcreteEmployee(String name, String email, Long legajo) {
            super(name, email, legajo);
        }
    }

    private ConcreteEmployee employee;

    @BeforeEach
    void setUp() {
        employee = new ConcreteEmployee("Juan Pérez", "juan.perez@davinci.edu.ar", 1005L);
    }

    @Test
    @DisplayName("generateExcuse debe crear una instancia de Excuse con el empleado y TypeExcuse simulado")
    void generateExcuseShouldCreateExcuseObjectWithMock() {
        // Arrange
        TrivialExcuse mockedTypeExcuse = Mockito.mock(TrivialExcuse.class);
        Excuse createdExcuse = employee.generateExcuse(mockedTypeExcuse);
        // Assert
        assertNotNull(createdExcuse, "El método debe retornar una instancia de Excuse.");
        assertEquals(employee, createdExcuse.getEmployee(), "El empleado en la excusa debe ser la instancia actual.");
        assertEquals(mockedTypeExcuse, createdExcuse.getTypeExcuse(), "El TypeExcuse debe ser el objeto Mock pasado como argumento.");
        assertEquals(ExcuseStatus.Pending, createdExcuse.getStatus(), "El status inicial debe ser PENDIENTE.");
        assertNotNull(createdExcuse.getRegisterDate(), "La fecha de registro debe haberse establecido.");
    }
}
