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
package org.eclipse.tractusx.traceability.common.utilities;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MaskerTest {

    @Test
    void givenString_whenMask_thenReturnsMaskedString() {
        // given
        String stringToMask = "String to be masked";
        String expectedOutcome = "Stri***************";

        // when
        String result = Masker.mask(stringToMask);

        // then
        assertThat(result).isEqualTo(expectedOutcome);
    }

    @Test
    void givenObject_whenMask_thenReturnsMaskedObjectString() {
        // given
        Object objectToMask = new TestObjectClass("name", "value");
        assertThat(objectToMask).hasToString("MaskerTest.TestObjectClass(name=name, value=value)");
        String expectedOutcome = "Mask**********************************************";

        // when
        String result = Masker.mask(objectToMask);

        assertThat(result).isEqualTo(expectedOutcome);
    }

    @Data
    @AllArgsConstructor
    private static class TestObjectClass {
        String name;
        String value;
    }

}
