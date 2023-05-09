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

package org.eclipse.tractusx.traceability.testdata;

import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.AffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.Investigation;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.Notification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.Severity;

import java.time.Instant;
import java.util.List;

public class InvestigationTestDataFactory {
    public static Investigation createInvestigationTestData(InvestigationStatus investigationStatus, InvestigationStatus notificationInvestigationStatus, final String bpnString) {
        InvestigationId investigationId = new InvestigationId(1L);
        BPN bpn = new BPN(bpnString);
        InvestigationSide investigationSide = InvestigationSide.SENDER;
        String description = "Test Investigation";
        Instant createdAt = Instant.now();
        List<String> assetIds = List.of("asset123", "asset456");

        Notification notification = Notification.builder()
                .id("1")
                .notificationReferenceId("notificationId")
                .senderBpnNumber("senderBPN")
                .senderManufacturerName("senderManufacturerName")
                .receiverBpnNumber("recipientBPN")
                .receiverManufacturerName("receiverManufacturerName")
                .edcUrl("senderAddress")
                .contractAgreementId("agreement")
                .description(description)
                .investigationStatus(investigationStatus)
                .affectedParts(List.of(new AffectedPart("part123")))
                .targetDate(Instant.now())
                .severity(Severity.MINOR)
                .edcNotificationId("1")
                .messageId("messageId")
                .isInitial(true)
                .build();
        List<Notification> notifications = List.of(notification);
/*
		List<Notification> notifications = List.of(new Notification(
                "1",
                "notificationId",
                "senderBPN",
                "senderManufacturerName",
                "recipientBPN",
                "receiverManufacturerName",
                "senderAddress",
                "agreement",
                "Test Notification",
                notificationInvestigationStatus,
                List.of(new AffectedPart("part123")),
                Instant.now(),
                Severity.MINOR,
                "1",
                null,
                null,
                "messageId",
                true
		));*/

        return Investigation.builder()
                .investigationId(investigationId)
                .bpn(bpn)
                .investigationStatus(investigationStatus)
                .investigationSide(investigationSide)
                .description(description)
                .createdAt(createdAt)
                .assetIds(assetIds)
                .notifications(notifications)
                .build();
    }


    public static Investigation createInvestigationTestDataWithNotificationList(InvestigationStatus investigationStatus, String bpnString, List<Notification> notifications) {
        InvestigationId investigationId = new InvestigationId(1L);
        BPN bpn = new BPN(bpnString);
        InvestigationSide investigationSide = InvestigationSide.SENDER;
        String description = "Test Investigation";
        Instant createdAt = Instant.now();
        List<String> assetIds = List.of("asset123", "asset456");

        return Investigation.builder()
                .investigationId(investigationId)
                .bpn(bpn)
                .investigationStatus(investigationStatus)
                .investigationSide(investigationSide)
                .description(description)
                .createdAt(createdAt)
                .assetIds(assetIds)
                .notifications(notifications)
                .build();
    }

    public static Investigation createInvestigationTestData(InvestigationStatus investigationStatus, InvestigationStatus notificationInvestigationStatus) {
        InvestigationId investigationId = new InvestigationId(1L);
        BPN bpn = new BPN("bpn123");
        InvestigationSide investigationSide = InvestigationSide.SENDER;
        String description = "Test Investigation";
        Instant createdAt = Instant.now();
        List<String> assetIds = List.of("asset123", "asset456");

        Notification notification = Notification.builder()
                .id("1")
                .notificationReferenceId("notificationId")
                .senderBpnNumber("senderBPN")
                .senderManufacturerName("senderManufacturerName")
                .receiverBpnNumber("recipientBPN")
                .receiverManufacturerName("receiverManufacturerName")
                .edcUrl("senderAddress")
                .contractAgreementId("agreement")
                .description(description)
                .investigationStatus(notificationInvestigationStatus)
                .affectedParts(List.of(new AffectedPart("part123")))
                .severity(Severity.MINOR)
                .edcNotificationId("123")
                .messageId("messageId")
                .isInitial(true)
                .build();
        List<Notification> notifications = List.of(notification);

/*		List<Notification> notifications = List.of(new Notification(
                "1",
                "notificationId",
                "senderBPN",
                "senderManufacturerName",
                "recipientBPN",
                "receiverManufacturerName",
                "senderAddress",
                "agreement",
                "Test Notification",
                notificationInvestigationStatus,
                List.of(new AffectedPart("part123")),
                Instant.now(),
                Severity.MINOR,
                "123",
                null,
                null,
                "messageId",
                true
		));*/

        return Investigation.builder()
                .investigationId(investigationId)
                .bpn(bpn)
                .investigationStatus(investigationStatus)
                .investigationSide(investigationSide)
                .description(description)
                .createdAt(createdAt)
                .assetIds(assetIds)
                .notifications(notifications)
                .build();
    }

    public static Investigation createInvestigationTestData(InvestigationSide investigationSide) {
        InvestigationId investigationId = new InvestigationId(1L);
        BPN bpn = new BPN("bpn123");
        String closeReason = null;
        String acceptReason = null;
        String declineReason = null;
        String description = "Test Investigation";
        Instant createdAt = Instant.now();
        List<String> assetIds = List.of("asset123", "asset456");


        Notification notification = Notification.builder()
                .id("1")
                .notificationReferenceId("notificationId")
                .senderBpnNumber("senderBPN")
                .senderManufacturerName("senderManufacturerName")
                .receiverBpnNumber("recipientBPN")
                .receiverManufacturerName("receiverManufacturerName")
                .edcUrl("senderAddress")
                .contractAgreementId("agreement")
                .description(description)
                .investigationStatus(InvestigationStatus.ACKNOWLEDGED)
                .affectedParts(List.of(new AffectedPart("part123")))
                .severity(Severity.MINOR)
                .edcNotificationId("123")
                .messageId("messageId")
                .isInitial(true)
                .build();
        List<Notification> notifications = List.of(notification);

/*        List<Notification> notifications = List.of(new Notification(
                "1",
                "notificationId",
                "senderBPN",
                "senderManufacturerName",
                "recipientBPN",
                "receiverManufacturerName",
                "senderAddress",
                "agreement",
                "Test Notification",
                InvestigationStatus.ACKNOWLEDGED,
                List.of(new AffectedPart("part123")),
                Instant.now(),
                Severity.MINOR,
                "123",
                null,
                null,
                "messageId",
                true
        ));*/

        return Investigation.builder()
                .investigationId(investigationId)
                .bpn(bpn)
                .investigationStatus(InvestigationStatus.ACKNOWLEDGED)
                .investigationSide(investigationSide)
                .description(description)
                .createdAt(createdAt)
                .assetIds(assetIds)
                .notifications(notifications)
                .build();
    }
}
