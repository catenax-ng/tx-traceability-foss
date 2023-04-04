package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ContractNegotiationTest {

    private static final ContractNegotiation.Type type = ContractNegotiation.Type.CONSUMER;
    private static final String id = "id";
    private static final String counterPartyId = "counterPartyId";
    private static final String counterPartyAddress = "counterPartyAddress";
    private static final String correlationId = "correlationId";
    private static final String protocol = "protocol";

    static ContractNegotiation contractNegotiation;

    @BeforeAll
    static void beforeAll() {
        contractNegotiation = ContractNegotiation.Builder.newInstance()
            .type(type)
            .id(id)
            .counterPartyId(counterPartyId)
            .counterPartyAddress(counterPartyAddress)
            .correlationId(correlationId)
            .protocol(protocol)
            .build();
    }

    @Test
    void getType() {
        assertEquals(type.name(), contractNegotiation.getType().name());
    }

    @Test
    void getId() {
        assertEquals(id, contractNegotiation.getId());
    }

    @Test
    void getCounterPartyId() {
        assertEquals(counterPartyId, contractNegotiation.getCounterPartyId());
    }

    @Test
    void getCounterPartyAddress() {
        assertEquals(counterPartyAddress, contractNegotiation.getCounterPartyAddress());
    }

    @Test
    void getCorrelationId() {
        assertEquals(correlationId, contractNegotiation.getCorrelationId());
    }

    @Test
    void getProtocol() {
        assertEquals(protocol, contractNegotiation.getProtocol());
    }

}
