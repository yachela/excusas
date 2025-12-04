package davinci.edu.ar.excusasSA.factory;

import davinci.edu.ar.excusasSA.model.excuse.typeExcuse.TypeExcuse;
import davinci.edu.ar.excusasSA.model.excuse.typeExcuse.TrivialExcuse;
import davinci.edu.ar.excusasSA.model.excuse.typeExcuse.impl.PowerOutage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitario para la clase TypeExcuseFactory, que asegura que se
 * crean las subclases de TypeExcuse correctas y que se manejan los errores de entrada.
 */
class TypeExcuseFactoryTest {

    private TypeExcuseFactory factory;

    @BeforeEach
    void setUp() {
        // Inicialización de la fábrica a probar
        factory = new TypeExcuseFactory();
    }

    // ====================================================================
    // 1. Tests de Creación Exitosa
    // ====================================================================

    /**
     * Verifica la creación de una instancia para un motivo simple (ej: TRIVIAL).
     */
    @Test
    void createTypeExcuse_shouldReturnTrivialExcuse_whenMotiveIsTrivial() {
        // ARRANGE
        // Asumimos que "TRIVIAL" está en TypeExcuseEnum y devuelve TrivialExcuse.
        final String motive = "TRIVIAL";

        // ACT
        TypeExcuse result = factory.createTypeExcuse(motive);

        // ASSERT
        assertNotNull(result, "El resultado no debe ser nulo.");
        assertTrue(result instanceof TrivialExcuse, "El tipo de excusa creado debe ser TrivialExcuse.");
    }

    /**
     * Verifica la creación de una instancia para otro motivo (ej: POWER_OUTAGE).
     */
    @Test
    void createTypeExcuse_shouldReturnPowerOutageExcuse_whenMotiveIsPowerOutage() {
        // ARRANGE
        // Asumimos que "POWER_OUTAGE" está en TypeExcuseEnum y devuelve PowerOutageExcuse.
        final String motive = "POWER_OUTAGE";

        // ACT
        TypeExcuse result = factory.createTypeExcuse(motive);

        // ASSERT
        assertNotNull(result, "El resultado no debe ser nulo.");
        assertTrue(result instanceof PowerOutage, "El tipo de excusa creado debe ser PowerOutageExcuse.");
    }

    /**
     * Verifica que la fábrica maneja la entrada en minúsculas y las normaliza a mayúsculas
     * para coincidir con el Enum (case-insensitivity handling).
     */
    @Test
    void createTypeExcuse_shouldHandleLowerCaseInput() {
        // ARRANGE
        final String motive = "trivial"; // Entrada en minúsculas

        // ACT
        TypeExcuse result = factory.createTypeExcuse(motive);

        // ASSERT
        assertTrue(result instanceof TrivialExcuse, "Debe manejar entrada en minúsculas y devolver TrivialExcuse.");
    }

    /**
     * Verifica que la fábrica maneja entradas con espacios extra, gracias al .trim().
     */
    @Test
    void createTypeExcuse_shouldHandleInputWithExtraSpaces() {
        // ARRANGE
        final String motive = "  POWER_OUTAGE  "; // Espacios al inicio y al final

        // ACT
        TypeExcuse result = factory.createTypeExcuse(motive);

        // ASSERT
        assertTrue(result instanceof PowerOutage, "Debe manejar entradas con espacios y devolver PowerOutageExcuse.");
    }

    // ====================================================================
    // 2. Tests de Manejo de Errores
    // ====================================================================

    /**
     * Verifica que se lance IllegalArgumentException cuando el motivo es nulo.
     */
    @Test
    void createTypeExcuse_shouldThrowException_whenMotiveIsNull() {
        // ARRANGE
        final String nullMotive = null;

        // ACT & ASSERT
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> factory.createTypeExcuse(nullMotive),
                "Debe lanzar IllegalArgumentException si el motivo es nulo.");

        assertTrue(thrown.getMessage().contains("cannot be empty"));
    }

    /**
     * Verifica que se lance IllegalArgumentException cuando el motivo es una cadena vacía.
     */
    @Test
    void createTypeExcuse_shouldThrowException_whenMotiveIsEmpty() {
        // ARRANGE
        final String emptyMotive = "";

        // ACT & ASSERT
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> factory.createTypeExcuse(emptyMotive),
                "Debe lanzar IllegalArgumentException si el motivo es una cadena vacía.");

        assertTrue(thrown.getMessage().contains("cannot be empty"));
    }

    /**
     * Verifica que se lance IllegalArgumentException cuando el motivo no es un valor válido
     * definido en el TypeExcuseEnum.
     */
    @Test
    void createTypeExcuse_shouldThrowException_whenMotiveIsInvalid() {
        // ARRANGE
        final String invalidMotive = "NO_EXISTE_ESTE_MOTIVO";

        // ACT & ASSERT
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> factory.createTypeExcuse(invalidMotive),
                "Debe lanzar IllegalArgumentException si el motivo no es válido.");

        assertTrue(thrown.getMessage().contains("Invalid or unsupported reason for excuse"));
    }

    /**
     * Verifica que se lance IllegalArgumentException cuando el motivo son solo espacios en blanco.
     */
    @Test
    void createTypeExcuse_shouldThrowException_whenMotiveIsWhitespace() {
        // ARRANGE
        final String whitespaceMotive = "   ";

        // ACT & ASSERT
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> factory.createTypeExcuse(whitespaceMotive),
                "Debe lanzar IllegalArgumentException si el motivo es solo espacios en blanco.");

        assertTrue(thrown.getMessage().contains("cannot be empty"));
    }
}