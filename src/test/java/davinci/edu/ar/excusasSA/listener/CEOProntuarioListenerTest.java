package davinci.edu.ar.excusasSA.listener;

import davinci.edu.ar.excusasSA.event.ProntuarioCreatedEvent;
import davinci.edu.ar.excusasSA.model.prontuario.Prontuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class CEOProntuarioListenerTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream standardOut = System.out;

    @Mock
    private Prontuario mockProntuario;
    @Mock
    private ProntuarioCreatedEvent mockEvent;
    @InjectMocks
    private CEOProntuarioListener listener;

    @BeforeEach
    void setUp() {
        // ARRANGE
        MockitoAnnotations.openMocks(this);
        // ARRANGE
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    void tearDown() {
        // ARRANGE
        System.setOut(standardOut);
    }

    @Test
    void onProntuarioCreated_shouldPrintProntuarioDetails() {
        // ARRANGE
        final String expectedProntuarioString = "Prontuario{...detalles...}";
        final String expectedOutputStart = "A new prontuario has been added";
        when(mockEvent.getProntuario()).thenReturn(mockProntuario);
        when(mockProntuario.toString()).thenReturn(expectedProntuarioString);
        listener.onProntuarioCreated(mockEvent);
        // ASSERT
        String actualOutput = outputStreamCaptor.toString().trim();
        assertTrue(actualOutput.contains(expectedOutputStart),
                "La salida debe contener el mensaje inicial de notificación.");
        assertTrue(actualOutput.contains(expectedProntuarioString),
                "La salida debe contener la representación en String del Prontuario.");
    }
}
