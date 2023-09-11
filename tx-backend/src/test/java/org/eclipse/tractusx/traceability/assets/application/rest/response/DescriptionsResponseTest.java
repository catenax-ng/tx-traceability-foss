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

package org.eclipse.tractusx.traceability.assets.application.rest.response;


import assets.response.base.DescriptionsResponse;
import org.eclipse.tractusx.traceability.assets.application.asbuilt.mapper.AssetAsBuiltResponseMapper;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Descriptions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DescriptionsResponseTest {

    @Test
    void givenDescriptionsResponse_whenFrom_thenMapCorrectly() {
        // given
        final String id = "identifier";
        final String shortId = "shortIdentifier";
        final Descriptions response = new Descriptions(id, shortId);

        // when
        final DescriptionsResponse result = AssetAsBuiltResponseMapper.from(response);

        // then
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(response);
    }

}
