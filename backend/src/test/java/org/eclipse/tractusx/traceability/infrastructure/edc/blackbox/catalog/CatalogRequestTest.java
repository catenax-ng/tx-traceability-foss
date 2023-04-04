package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.catalog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CatalogRequestTest {

    private static final String protocol = "protocol";
    private static final String connectorId = "connectorId";
    private static final String connectorAddress = "connectorAddress";

    private CatalogRequest catalogRequest;

    @BeforeEach
    void setUp() {
        catalogRequest = CatalogRequest.Builder.newInstance()
                .protocol(protocol)
                .connectorId(connectorId)
                .connectorAddress(connectorAddress)
                .build();
    }

    @Test
    void getProtocol() {
        assertEquals(protocol, catalogRequest.getProtocol());
    }

    @Test
    void getConnectorId() {
        assertEquals(connectorId, catalogRequest.getConnectorId());
    }

    @Test
    void getConnectorAddress() {
        assertEquals(connectorAddress, catalogRequest.getConnectorAddress());
    }
}
