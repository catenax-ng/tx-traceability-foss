/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/
package org.eclipse.tractusx.traceability.qualitynotification.domain.contract.policy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.common.utilities.Masker;
import org.eclipse.tractusx.traceability.qualitynotification.domain.contract.asset.model.CreateEdcAssetException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.contract.asset.model.OdrlContext;
import org.eclipse.tractusx.traceability.qualitynotification.domain.contract.contract.model.EdcOperator;
import org.eclipse.tractusx.traceability.qualitynotification.domain.contract.policy.model.CreateEdcPolicyDefinitionException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.contract.policy.model.EdcCreatePolicyDefinitionRequest;
import org.eclipse.tractusx.traceability.qualitynotification.domain.contract.policy.model.EdcPolicy;
import org.eclipse.tractusx.traceability.qualitynotification.domain.contract.policy.model.EdcPolicyPermission;
import org.eclipse.tractusx.traceability.qualitynotification.domain.contract.policy.model.EdcPolicyPermissionConstraint;
import org.eclipse.tractusx.traceability.qualitynotification.domain.contract.policy.model.EdcPolicyPermissionConstraintExpression;
import org.eclipse.tractusx.traceability.common.properties.EdcProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

import static org.eclipse.tractusx.traceability.common.config.JsonLdConfigurationTraceX.NAMESPACE_ODRL;
import static org.eclipse.tractusx.traceability.common.config.EdcRestTemplateConfiguration.EDC_REST_TEMPLATE;

@Slf4j
@Component
public class EdcPolicyDefinitionService {

    private static final String USE_ACTION = "USE";
    private static final String POLICY_TYPE = "Policy";
    private static final String POLICY_DEFINITION_TYPE = "PolicyDefinitionRequestDto";
    private static final String ATOMIC_CONSTRAINT = "AtomicConstraint";
    private static final String PURPOSE_CONSTRAINT = "PURPOSE";
    private static final String ID_TRACE_CONSTRAINT = "ID 3.0 Trace";
    private static final String CONSTRAINT = "Constraint";
    private static final String ASSET_SELECTOR_EQUALITY_OPERATOR = "odrl:eq";
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final EdcProperties edcProperties;

    @Autowired
    public EdcPolicyDefinitionService(ObjectMapper objectMapper, @Qualifier(EDC_REST_TEMPLATE) RestTemplate edcRestTemplate, EdcProperties edcProperties) {
        this.objectMapper = objectMapper;
        this.restTemplate = edcRestTemplate;
        this.edcProperties = edcProperties;
    }

    public String createAccessPolicy() throws JsonProcessingException {

        EdcPolicyPermissionConstraintExpression constraint = EdcPolicyPermissionConstraintExpression.builder()
                .leftOperand(PURPOSE_CONSTRAINT)
                .rightOperand(ID_TRACE_CONSTRAINT)
                .operator(new EdcOperator(ASSET_SELECTOR_EQUALITY_OPERATOR))
                .type(CONSTRAINT)
                .build();

        EdcPolicyPermissionConstraint edcPolicyPermissionConstraint = EdcPolicyPermissionConstraint.builder()
                .orExpressions(List.of(constraint))
                .type(ATOMIC_CONSTRAINT)
                .build();

        EdcPolicyPermission odrlPermissions = EdcPolicyPermission
                .builder()
                .action(USE_ACTION)
                .edcPolicyPermissionConstraints(edcPolicyPermissionConstraint)
                .build();

        EdcPolicy edcPolicy = EdcPolicy.builder().odrlPermissions(List.of(odrlPermissions)).type(POLICY_TYPE).build();

        String accessPolicyId = UUID.randomUUID().toString();
        OdrlContext odrlContext = new OdrlContext(NAMESPACE_ODRL);

        EdcCreatePolicyDefinitionRequest edcCreatePolicyDefinitionRequest = EdcCreatePolicyDefinitionRequest
                .builder()
                .policyDefinitionId(accessPolicyId)
                .policy(edcPolicy)
                .odrlContext(odrlContext)
                .type(POLICY_DEFINITION_TYPE)
                .build();
        log.info("EdcCreatePolicyDefinitionRequest {}", objectMapper.writeValueAsString(edcCreatePolicyDefinitionRequest));
        final ResponseEntity<String> createPolicyDefinitionResponse;
        try {
            createPolicyDefinitionResponse = restTemplate.postForEntity(edcProperties.getPolicyDefinitionsPath(), edcCreatePolicyDefinitionRequest, String.class);
        } catch (RestClientException e) {
            log.error("Failed to create EDC notification asset policy. Reason: ", e);

            throw new CreateEdcPolicyDefinitionException(e);
        }

        HttpStatusCode responseCode = createPolicyDefinitionResponse.getStatusCode();

        if (responseCode.value() == 409) {
            log.info("Notification asset policy definition already exists in the EDC");

            throw new CreateEdcPolicyDefinitionException("Notification asset policy definition already exists in the EDC");
        }

        if (responseCode.value() == 200) {
            return accessPolicyId;
        }

        log.error("Failed to create EDC notification policy definition for notification asset. Body: {}, status: {}", Masker.mask(createPolicyDefinitionResponse.getBody()), createPolicyDefinitionResponse.getStatusCode());

        throw new CreateEdcAssetException("Failed to create EDC notification policy definition for asset");
    }

    public void deleteAccessPolicy(String accessPolicyId) {
        String deleteUri = UriComponentsBuilder.fromPath(edcProperties.getPolicyDefinitionsPath())
                .pathSegment("{accessPolicyId}")
                .buildAndExpand(accessPolicyId)
                .toUriString();

        try {
            restTemplate.delete(deleteUri);
        } catch (RestClientException e) {
            log.error("Failed to delete EDC notification asset policy {}. Reason: ", accessPolicyId, e);
        }
    }
}
