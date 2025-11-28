package davinci.edu.ar.excusasSA.model.excuse.typeExcuse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ImplausibleExcuseTest {
    private ImplausibleExcuse implausibleExcuse;

    @BeforeEach
    void setUp() {
        // Arrange
        implausibleExcuse = new ImplausibleExcuse();
    }

    @Test
    void isTrivial_ShouldReturnTrue() {
        // Act & Assert
        assertTrue(implausibleExcuse.isImplausible(),
                "A ImplausibleExcuse should always return true for isImplausible.");
    }

    @Test
    void getAffair_ShouldReturnCorrectValue() {
        // Act & Assert
        assertEquals("Approved for creativity", implausibleExcuse.getAffair(),
                "The affair must match the ImplausibleExcuse implementation.");
    }

    @Test
    void getBody_ShouldReturnCorrectValue() {
        // Act & Assert
        assertEquals("good imagination crack", implausibleExcuse.getBody(),
                "The body must match the ImplausibleExcuse implementation.");
    }

    @Test
    void toString_ShouldReturnClassName() {
        // Act & Assert
        assertEquals("ImplausibleExcuse", implausibleExcuse.toString(),
                "toString must return the class name.");
    }

    @Test
    void otherClassificationMethods_ShouldReturnFalse() {
        // Act & Assert
        assertFalse(implausibleExcuse.isTrivial(), "isTrivial should be false.");
        assertFalse(implausibleExcuse.isComplex(), "isComplex should be false.");
        assertFalse(implausibleExcuse.isModerate(), "isModerate should be false.");
    }
}
