package org.eclipse.tractusx.traceability.investigations.adapters.rest.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeverityValidatorImplTest {

    private SeverityValidatorImpl validator = new SeverityValidatorImpl();

    @Test
    void testSeveritySuccess() {
        boolean isMinor = validator.isValid("MINOR", null);
        boolean isMajor = validator.isValid("MAJOR", null);
        boolean isLifeThreatening = validator.isValid("LIFE-THREATENING", null);
        boolean isCritical = validator.isValid("CRITICAL", null);
        boolean isLifeThreateningUnderscore = validator.isValid("LIFE_THREATENING", null);

        boolean wrongParameter = validator.isValid("anything", null);
        assertTrue(isMinor, "MINOR should pass validation");
        assertTrue(isMajor, "MAJOR should pass validation");
        assertTrue(isLifeThreatening, "LIFE-THREATENING should pass validation");
        assertTrue(isCritical, "CRITICAL should pass validation");
        assertTrue(isLifeThreateningUnderscore, "LIFE_THREATENING should pass validation");
        assertFalse(wrongParameter, "anything should not pass validation");
    }

}
