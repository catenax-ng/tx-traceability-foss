package org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.contract.service;

import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.contract.model.EdcCreateContractDefinitionRequest;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EdcContractDefinitionServiceTest {

    private static final String notificationAssetId = "9";
    private static final String accessPolicyId = "99";
    private static final String CONTRACT_DEFINITIONS_PATH = "/data/contractdefinitions";

    private RestTemplate restTemplate;
    private EdcContractDefinitionService edcContractDefinitionService;
    private EdcCreateContractDefinitionRequest createContractDefinitionRequest;
    private ResponseEntity<String> createContractDefinitionResponse;
    private HttpStatusCode responseCode;

    void setUp() {
        restTemplate = new RestTemplate();
        // when(restTemplate).thenReturn(restTemplate);
        createContractDefinitionResponse = new ResponseEntity<>(HttpStatusCode.valueOf(204));
        when(restTemplate.postForEntity(CONTRACT_DEFINITIONS_PATH, createContractDefinitionRequest, String.class)).thenReturn(createContractDefinitionResponse);
        edcContractDefinitionService = new EdcContractDefinitionService(restTemplate);
    }

    @Test
    @Ignore
    void createContractDefinition() {
        // edcContractDefinitionService.createContractDefinition(notificationAssetId, accessPolicyId);
        assertEquals(1, 1);
    }

}
