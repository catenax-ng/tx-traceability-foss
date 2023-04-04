package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CriterionTest {

    private static final String LEFT = "abc";
    private static final String OP_EQUALS = "=";
    private static final String RIGHT = "xyz";

    private Criterion criterion;

    @BeforeEach
    void setUp() {
        criterion = new Criterion(CriterionTest.LEFT, CriterionTest.OP_EQUALS, CriterionTest.RIGHT);
    }

    @Test
    void getOperandLeft() {
        assertEquals(CriterionTest.LEFT, criterion.getOperandLeft());
    }

    @Test
    void getOperator() {
        assertEquals(CriterionTest.OP_EQUALS, criterion.getOperator());
    }

    @Test
    void getOperandRight() {
        assertEquals(CriterionTest.RIGHT, criterion.getOperandRight());
    }

}
