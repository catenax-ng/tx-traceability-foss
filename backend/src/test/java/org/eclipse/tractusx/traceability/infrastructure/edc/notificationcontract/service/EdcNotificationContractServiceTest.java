package org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service;

import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.controller.model.CreateNotificationContractRequest;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.controller.model.CreateNotificationContractResponse;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.controller.model.NotificationMethod;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.controller.model.NotificationType;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.asset.service.EdcNotitifcationAssetService;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.contract.service.EdcContractDefinitionService;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.policy.service.EdcPolicyDefinitionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EdcNotificationContractServiceTest {

    @Mock
    EdcNotitifcationAssetService edcNotitifcationAssetService;

    @Mock
    EdcPolicyDefinitionService edcPolicyDefinitionService;

    @Mock
    EdcContractDefinitionService edcContractDefinitionService;

    @Mock
    private Environment environment;

    private EdcNotificationContractService edcNotificationContractService;
    private CreateNotificationContractRequest request;

    private static final String notificationAssetId = "9";
    private static final String accessPolicyId = "99";
    private static final String contractDefinitionId = "999";

    @BeforeEach
    void setUp() {
        NotificationType notificationType = NotificationType.QUALITY_INVESTIGATION;
        NotificationMethod notificationMethod = NotificationMethod.RESOLVE;
        request = new CreateNotificationContractRequest(notificationType, notificationMethod);
        when(edcNotitifcationAssetService.createNotificationAsset(notificationMethod, request.notificationType())).thenReturn(notificationAssetId);
        when(edcPolicyDefinitionService.createAccessPolicy(notificationAssetId)).thenReturn(accessPolicyId);
        when(edcContractDefinitionService.createContractDefinition(notificationAssetId, accessPolicyId)).thenReturn(contractDefinitionId);
        edcNotificationContractService = new EdcNotificationContractService(
            edcNotitifcationAssetService, edcPolicyDefinitionService, edcContractDefinitionService
        );
    }

    @Test
    void testHandle() {
        CreateNotificationContractResponse response = edcNotificationContractService.handle(request);
        assertEquals(notificationAssetId, response.notificationAssetId());
        assertEquals(accessPolicyId, response.accessPolicyId());
        assertEquals(contractDefinitionId, response.contractDefinitionId());
    }
}
