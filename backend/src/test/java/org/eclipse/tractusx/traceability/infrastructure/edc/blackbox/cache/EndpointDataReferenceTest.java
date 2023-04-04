package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.cache;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EndpointDataReferenceTest {

    private static final String endpoint = "endpoint";
    private static final String id = "id";
    private static final String authkey = "authkey";
    private static final String authcode = "authcode";
    private static final Map<String, String> properties = new HashMap<>();
    private EndpointDataReference endpointDataReference;

    @BeforeEach
    void setUp() {
        properties.put("key0", "value0");
        endpointDataReference = EndpointDataReference.Builder.newInstance()
            .id(id)
            .endpoint(endpoint)
            .authKey(authkey)
            .authCode(authcode)
            .properties(properties)
            .build();
    }

    @Test
    void getId() {
        assertEquals(id, endpointDataReference.getId());
    }

    @Test
    void getEndpoint() {
        assertEquals(endpoint, endpointDataReference.getEndpoint());
    }

    @Test
    void getAuthKey() {
        assertEquals(authkey, endpointDataReference.getAuthKey());
    }

    @Test
    void getAuthCode() {
        assertEquals(authcode, endpointDataReference.getAuthCode());
    }

    @Test
    void getProperties() {
        assertEquals(properties.keySet().size(), endpointDataReference.getProperties().keySet().size());
    }

}
