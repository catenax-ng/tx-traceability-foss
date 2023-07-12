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

package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.config;

import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.IrsPolicy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class IrsPolicyConfigTest {

    @Test
    void givenPolicyConfig_whenGetPolicy_thenGetCorrectPolicy() {
        // given
        final String policyName = "TRACEX ID 3.0";
        final String ttl = "2023-07-03T16:01:05.309Z";
        final IrsPolicyConfig policyConfig = new IrsPolicyConfig(List.of(
                IrsPolicy.builder().policyId(policyName).ttl(ttl).build()
        ));

        // when
        final List<IrsPolicy> result = policyConfig.getPolicies();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.stream().findFirst().get().getPolicyId()).isEqualTo(policyName);
        assertThat(result.stream().findFirst().get().getTtl()).isEqualTo(ttl);
    }
}
