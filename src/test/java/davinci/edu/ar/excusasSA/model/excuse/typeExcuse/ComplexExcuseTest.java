package davinci.edu.ar.excusasSA.model.excuse.typeExcuse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ComplexExcuseTest {

    private ComplexExcuse complexExcuse;

    @BeforeEach
    void setUp() {
        // Arrange
        complexExcuse = new ComplexExcuse();
    }

    @Test
    void isComplex_ShouldReturnTrue() {
        // Act & Assert
        assertTrue(complexExcuse.isComplex(),
                "A ComplexExcuse should always return true for isComplex.");
    }

    @Test
    void getAffair_ShouldReturnCorrectValue() {
        // Act & Assert
        assertEquals("Excuse Complex", complexExcuse.getAffair(),
                "The affair must match the ComplexExcuse implementation.");
    }

    @Test
    void getBody_ShouldReturnCorrectValue() {
        // Act & Assert
        assertEquals("the license was accepted", complexExcuse.getBody(),
                "The body must match the ComplexExcuse implementation.");
    }

    @Test
    void toString_ShouldReturnClassName() {
        // Act & Assert
        assertEquals("ComplexExcuse", complexExcuse.toString(),
                "toString must return the class name.");
    }

    @Test
    void otherClassificationMethods_ShouldReturnFalse() {
        // Act & Assert
        assertFalse(complexExcuse.isImplausible(), "isImplausible should be false.");
        assertFalse(complexExcuse.isTrivial(), "isTrivial should be false.");
        assertFalse(complexExcuse.isModerate(), "isModerate should be false.");
    }

}
