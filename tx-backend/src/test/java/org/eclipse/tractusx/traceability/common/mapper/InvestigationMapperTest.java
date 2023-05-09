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
package org.eclipse.tractusx.traceability.common.mapper;

import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.AffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.Investigation;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.Notification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.Severity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvestigationMapperTest {

    @InjectMocks
    private InvestigationMapper mapper;

    @Mock
    private Clock clock;

    @Test
    void testToReceiverInvestigation() {
        // Given
        String sender = "BPNL000000000001";
        String receiver = "BPNL000000000002";
        String description = "Test investigation";
        Notification notification = Notification.builder()
                .id("1")
                .notificationReferenceId("Test notification")
                .investigationStatus(InvestigationStatus.RECEIVED)
                .affectedParts(List.of(new AffectedPart("123")))
                .senderManufacturerName("senderManufacturerName")
                .senderBpnNumber(sender)
                .receiverBpnNumber(receiver)
                .receiverManufacturerName("receiverManufacturerName")
                .severity(Severity.MINOR)
                .isInitial(false)
                .messageId("1")
                .build();
/*        Notification notification = new Notification("1",
                "Test notification",
                sender, ""senderManufacturerName"",
                receiver,
                "receiverManufacturerName",
                "",
                "",
                "",
                InvestigationStatus.RECEIVED,
                List.of(new AffectedPart("123")),
                Instant.parse("2022-03-01T12:00:00Z"),
                Severity.MINOR,
                "1",
                null,
                null,
                "messageId",
                false
        );*/
        when(clock.instant()).thenReturn(Instant.parse("2022-03-01T12:00:00Z"));

        // When
        Investigation result = mapper.toInvestigation(new BPN(receiver), description, notification);

        // Then
        assertEquals(InvestigationStatus.RECEIVED, result.getInvestigationStatus());
        assertEquals(InvestigationSide.RECEIVER, result.getInvestigationSide());
        assertEquals(description, result.getDescription());
        assertEquals(Instant.parse("2022-03-01T12:00:00Z"), result.getCreatedAt());
        assertEquals(List.of("123"), result.getAssetIds());
        assertEquals(List.of(notification), result.getNotifications());
    }
}


