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

package org.eclipse.tractusx.traceability.notification.investigation.rest.model;

import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class UpdateNotificationStatusTest {

    @Test
    void testToInvestigationStatusACCEPTED() {
        String accepted = "ACCEPTED";
        NotificationStatus actualInvestigationStatus = NotificationStatus.fromStringValue(accepted);
        assertThat(actualInvestigationStatus.name()).isEqualTo("ACCEPTED");
    }

    @Test
    void testToInvestigationStatusACKNOWLEDGED() {
        String acknowledged = "ACKNOWLEDGED";
        NotificationStatus actualInvestigationStatus = NotificationStatus.fromStringValue(acknowledged);
        assertThat(actualInvestigationStatus.name()).isEqualTo("ACKNOWLEDGED");
    }

    @Test
    void testToInvestigationStatusDECLINED() {
        String declined = "DECLINED";
        NotificationStatus actualInvestigationStatus = NotificationStatus.fromStringValue(declined);
        assertThat(actualInvestigationStatus.name()).isEqualTo("DECLINED");
    }


}
