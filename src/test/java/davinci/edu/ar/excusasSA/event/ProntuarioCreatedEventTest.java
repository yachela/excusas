package davinci.edu.ar.excusasSA.event;

import davinci.edu.ar.excusasSA.model.prontuario.Prontuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class ProntuarioCreatedEventTest {

    private final Object TEST_SOURCE = new Object();
    private ProntuarioCreatedEvent event;

    @Mock
    private Prontuario mockProntuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        event = new ProntuarioCreatedEvent(TEST_SOURCE, mockProntuario);
    }

    @Test
    void constructor_shouldSetSourceAndProntuarioCorrectly() {
        // ARRANGE
        // ACT & ASSERT
        assertEquals(mockProntuario, event.getProntuario(),
                "getProntuario() must return the Prontuario object passed to the constructor.");
        assertEquals(TEST_SOURCE, event.getSource(),
                "getSource() must return the event's source object.");
    }

    @Test
    void getProntuario_shouldReturnTheCorrectInstance() {
        // ARRANGE
        // ACT
        Prontuario retrievedProntuario = event.getProntuario();
        // ASSERT
        assertNotNull(retrievedProntuario, "The returned prontuario should not be null.");
        assertSame(mockProntuario, retrievedProntuario,
                "The returned object must be the same mocked instance.");
    }
}