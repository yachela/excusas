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
                "A TrivialExcuse should always return true for isTrivial.");
    }

    @Test
    void getAffair_ShouldReturnCorrectValue() {
        // Act & Assert
        assertEquals("reason for delay", trivialExcuse.getAffair(),
                "The affair must match the TrivialExcuse implementation.");
    }

    @Test
    void getBody_ShouldReturnCorrectValue() {
        // Act & Assert
        assertEquals("the license was accepted", trivialExcuse.getBody(),
                "The body must match the TrivialExcuse implementation.");
    }

    @Test
    void toString_ShouldReturnClassName() {
        // Act & Assert
        assertEquals("TrivialExcuse", trivialExcuse.toString(),
                "toString must return the class name.");
    }

    @Test
    void otherClassificationMethods_ShouldReturnFalse() {
        // Act & Assert
        assertFalse(trivialExcuse.isImplausible(), "isImplausible should be false.");
        assertFalse(trivialExcuse.isComplex(), "isComplex should be false.");
        assertFalse(trivialExcuse.isModerate(), "isModerate should be false.");
    }
}
