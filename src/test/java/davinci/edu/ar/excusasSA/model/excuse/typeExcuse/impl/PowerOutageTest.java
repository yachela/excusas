package davinci.edu.ar.excusasSA.model.excuse.typeExcuse.impl;

import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class PowerOutageTest {

    private PowerOutage powerOutage;

    @Mock
    private Excuse mockExcuse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        powerOutage = new PowerOutage();
    }

    @Test
    void isModerate_shouldReturnTrue() {
        assertTrue(powerOutage.isModerate(), "It must be classified as Moderate.");
        assertFalse(powerOutage.isTrivial(), "It must not be Trivial.");
    }

    @Test
    void toString_shouldReturnModerateExcuse() {
        assertEquals("ModerateExcuse", powerOutage.toString(), "toString must reflect the abstract base class.");
    }

    @Test
    void getDestinationEmail_shouldReturnEdesurEmail() {
        String expectedEmail = "EDESUR@mailfake.com.ar";
        assertEquals(expectedEmail, powerOutage.getDestinationEmail(mockExcuse));
    }

    @Test
    void getAffair_shouldReturnBlackoutQuestion() {
        String expectedAffair = "question about the blackout";
        assertEquals(expectedAffair, powerOutage.getAffair());
    }

    @Test
    void getBody_shouldReturnBlackoutBody() {
        String expectedBody = "was there a blackout in such neighborhood?";
        assertEquals(expectedBody, powerOutage.getBody());
    }
}
