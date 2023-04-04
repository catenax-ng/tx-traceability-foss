package org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.contract.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EdcContractDefinitionCriteriaTest {

    private static final String LEFT = "abc";
    private static final String OP_EQUALS = "=";
    private static final String RIGHT = "xyz";

    private EdcContractDefinitionCriteria edcContractDefinitionCriteria;

    @BeforeEach
    void setUp() {
        edcContractDefinitionCriteria = new EdcContractDefinitionCriteria(
                EdcContractDefinitionCriteriaTest.LEFT,
                EdcContractDefinitionCriteriaTest.OP_EQUALS,
                EdcContractDefinitionCriteriaTest.RIGHT
        );
    }

    @Test
    void getOperandLeft() {
        assertEquals(EdcContractDefinitionCriteriaTest.LEFT, edcContractDefinitionCriteria.getOperandLeft());
    }

    @Test
    void getOperator() {
        assertEquals(EdcContractDefinitionCriteriaTest.OP_EQUALS, edcContractDefinitionCriteria.getOperator());
    }

    @Test
    void getOperandRight() {
        assertEquals(EdcContractDefinitionCriteriaTest.RIGHT, edcContractDefinitionCriteria.getOperandRight());
    }
}
