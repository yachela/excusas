package davinci.edu.ar.excusasSA.model.excuse.typeExcuse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TrivialExcuseTest {

    private TrivialExcuse trivialExcuse;

    @BeforeEach
    void setUp() {
        // Arrange
        trivialExcuse = new TrivialExcuse();
    }

    @Test
    void isTrivial_ShouldReturnTrue() {
        // Act & Assert
        assertTrue(trivialExcuse.isTrivial(),
                "Una TrivialExcuse siempre debe retornar true para isTrivial.");
    }

    @Test
    void getAffair_ShouldReturnCorrectValue() {
        // Act & Assert
        assertEquals("reason for delay", trivialExcuse.getAffair(),
                "El asunto debe coincidir con la implementación de TrivialExcuse.");
    }

    @Test
    void getBody_ShouldReturnCorrectValue() {
        // Act & Assert
        assertEquals("the license was accepted", trivialExcuse.getBody(),
                "El cuerpo debe coincidir con la implementación de TrivialExcuse.");
    }

    @Test
    void toString_ShouldReturnClassName() {
        // Act & Assert
        assertEquals("TrivialExcuse", trivialExcuse.toString(),
                "toString debe devolver el nombre de la clase.");
    }

    @Test
    void otherClassificationMethods_ShouldReturnFalse() {
        // Act & Assert
        assertFalse(trivialExcuse.isImplausible(), "isImplausible debe ser false.");
        assertFalse(trivialExcuse.isComplex(), "isComplex debe ser false.");
        assertFalse(trivialExcuse.isModerate(), "isModerate debe ser false.");
    }
}