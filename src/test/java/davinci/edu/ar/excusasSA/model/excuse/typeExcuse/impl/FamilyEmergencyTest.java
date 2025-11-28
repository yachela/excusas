package davinci.edu.ar.excusasSA.model.excuse.typeExcuse.impl;

import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class FamilyEmergencyTest {

    private FamilyEmergency familyEmergency;

    @Mock
    private Excuse mockExcuse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        familyEmergency = new FamilyEmergency();
    }

    @Test
    void isModerate_shouldReturnTrue() {
        assertTrue(familyEmergency.isModerate(), "It must be classified as Moderate.");
        assertFalse(familyEmergency.isTrivial(), "It must not be Trivial.");
    }

    @Test
    void toString_shouldReturnModerateExcuse() {
        assertEquals("ModerateExcuse", familyEmergency.toString(), "toString must reflect the base abstract class.");
    }

    @Test
    void getDestinationEmail_shouldReturnHREmail() {
        String expectedEmail = "HR@company.com";
        assertEquals(expectedEmail, familyEmergency.getDestinationEmail(mockExcuse));
    }

    @Test
    void getAffair_shouldReturnEmergencyAffair() {
        String expectedAffair = "Notice of family emergency";
        assertEquals(expectedAffair, familyEmergency.getAffair());
    }

    @Test
    void getBody_shouldReturnEmergencyBody() {
        String expectedBody = "I need to take emergency leave";
        assertEquals(expectedBody, familyEmergency.getBody());
    }
}
