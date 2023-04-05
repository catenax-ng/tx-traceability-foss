package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.transfer;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class TransferRequestDtoTest {

    private static final String connectorAddress = "connectorAddress";
    private static final String id = "id";
    private static final String contractId = "contractId";
    private static final boolean managedResources = true;
    private static final String protocol = "ids-multipart";
    private static final String connectorId = "connectorId";
    private static final String assetId = "assetId";
    public static final String type = "type";

    private DataAddress dataDestination;
    private Map<String, String> properties;
    private TransferType transferType;
    private TransferRequestDto transferRequestDto;

    @BeforeEach
    void setUp() {
        properties = new HashMap<>();
        properties.put(id, id);
        transferType = TransferType.Builder.transferType().build();
        dataDestination = DataAddress.Builder.newInstance()
            .type(type)
            .build();
        transferRequestDto = TransferRequestDto.Builder.newInstance()
            .connectorAddress(connectorAddress)
            .id(id)
            .contractId(contractId)
            .dataDestination(dataDestination)
            .managedResources(managedResources)
            .properties(properties)
            .transferType(transferType)
            .protocol(protocol)
            .connectorId(connectorId)
            .assetId(assetId)
            .build();
    }

    @Test
    void getConnectorAddress() {
        assertEquals(connectorAddress, transferRequestDto.getConnectorAddress());
    }

    @Test
    void getId() {
        assertEquals(id, transferRequestDto.getId());
    }

    @Test
    void getContractId() {
        assertEquals(contractId, transferRequestDto.getContractId());
    }

    @Test
    void getDataDestination() {
        assertEquals(dataDestination, transferRequestDto.getDataDestination());
    }

    @Test
    void isManagedResources() {
        assertTrue(transferRequestDto.isManagedResources());
    }

    @Test
    void getProperties() {
        assertEquals(properties, transferRequestDto.getProperties());
    }

    @Test
    void getTransferType() {
        assertEquals(transferType, transferRequestDto.getTransferType());
    }

    @Test
    void getProtocol() {
        assertEquals(protocol, transferRequestDto.getProtocol());
    }

    @Test
    void getConnectorId() {
        assertEquals(connectorId, transferRequestDto.getConnectorId());
    }

    @Test
    void getAssetId() {
        assertEquals(assetId, transferRequestDto.getAssetId());
    }

}
