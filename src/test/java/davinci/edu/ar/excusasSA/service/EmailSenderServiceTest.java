package davinci.edu.ar.excusasSA.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailSenderServiceTest {

    private EmailSenderService emailSenderService;

    private final String VALID_DEST = "destino@ejemplo.com";
    private final String VALID_ORIGIN = "origen@empresa.com";
    private final String VALID_AFFAIR = "Notificación de excusa aprobada";
    private final String VALID_BODY = "Su excusa ha sido procesada exitosamente.";

    @BeforeEach
    void setUp() {
        emailSenderService = new EmailSenderService();
    }

    @Test
    void sendEmail_ShouldExecuteWithoutException_WhenInputsAreValid() {
        assertDoesNotThrow(() -> {
            emailSenderService.sendEmail(VALID_DEST, VALID_ORIGIN, VALID_AFFAIR, VALID_BODY);
        }, "El envío de correo válido no debería lanzar excepciones.");
    }
}
