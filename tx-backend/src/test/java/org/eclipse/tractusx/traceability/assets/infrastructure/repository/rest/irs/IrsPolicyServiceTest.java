/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.assets.infrastructure.repository.rest.irs;

import org.eclipse.tractusx.traceability.assets.domain.base.BpnRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.IRSApiClient;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.IrsPolicyService;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.config.IrsPolicyConfig;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.RegisterPolicyRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.PolicyResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.IrsPolicy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IrsPolicyServiceTest {

    @InjectMocks
    private IrsPolicyService irsPolicyService;

    @Mock
    private IRSApiClient irsClient;

    @Mock
    private IrsPolicyConfig irsPolicyConfig;

    @Test
    void givenNoPolicyExist_whenCreateIrsPolicyIfMissing_thenCreateIt() {
        // given
        final IrsPolicy policyToCreate = IrsPolicy.builder()
                .policyId("test")
                .ttl("2023-07-03T16:01:05.309Z")
                .build();
        when(irsClient.getPolicies()).thenReturn(List.of());
        when(irsPolicyConfig.getPolicies()).thenReturn(List.of(policyToCreate));

        // when
        irsPolicyService.createIrsPolicyIfMissing();

        // then
        verify(irsClient, times(1))
                .registerPolicy(RegisterPolicyRequest.from(policyToCreate));
    }

    @Test
    void givenPolicyExist_whenCreateIrsPolicyIfMissing_thenDoNotCreateIt() {
        // given
        final IrsPolicy policyToCreate = IrsPolicy.builder()
                .policyId("test")
                .ttl("2023-07-03T16:01:05.309Z")
                .build();
        final PolicyResponse existingPolicy = new PolicyResponse("test", Instant.parse("2023-07-03T16:01:05.309Z"), Instant.now());
        when(irsClient.getPolicies()).thenReturn(List.of(existingPolicy));
        when(irsPolicyConfig.getPolicies()).thenReturn(List.of(policyToCreate));

        // when
        irsPolicyService.createIrsPolicyIfMissing();

        // then
        verifyNoMoreInteractions(irsClient);
    }

    @Test
    void givenOutdatedPolicyExist_whenCreateIrsPolicyIfMissing_thenUpdateIt() {
        // given
        final IrsPolicy policyToCreate = IrsPolicy.builder()
                .policyId("test")
                .ttl("2123-07-03T16:01:05.309Z")
                .build();
        final PolicyResponse existingPolicy = new PolicyResponse("test", Instant.parse("2023-07-03T16:01:05.309Z"), Instant.now());
        when(irsClient.getPolicies()).thenReturn(List.of(existingPolicy));
        when(irsPolicyConfig.getPolicies()).thenReturn(List.of(policyToCreate));

        // when
        irsPolicyService.createIrsPolicyIfMissing();

        // then
        verify(irsClient, times(1)).deletePolicy("test");
        verify(irsClient, times(1)).registerPolicy(RegisterPolicyRequest.from(policyToCreate));
    }
}
