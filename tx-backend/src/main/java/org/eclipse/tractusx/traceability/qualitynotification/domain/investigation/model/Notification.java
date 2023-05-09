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

package org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.NotificationStatusTransitionNotAllowed;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@Data
public class Notification {
    private final String id;
    private String notificationReferenceId;
    private String senderBpnNumber;
    private final String senderManufacturerName;
    private String receiverBpnNumber;
    private final String receiverManufacturerName;
    private String edcUrl;
    private String contractAgreementId;
    @Builder.Default
    private final List<AffectedPart> affectedParts = new ArrayList<>();
    private String description;
    private InvestigationStatus investigationStatus;
    private String edcNotificationId;
    private LocalDateTime created;
    private LocalDateTime updated;
    private Instant targetDate;
    private Severity severity;
    private String messageId;
    private Boolean isInitial;

/*    public Notification(String id,
                        String notificationReferenceId,
                        String senderBpnNumber,
                        String senderManufacturerName,
                        String receiverBpnNumber,
                        String receiverManufacturerName,
                        String edcUrl,
                        String contractAgreementId,
                        String description,
                        InvestigationStatus investigationStatus,
                        List<AffectedPart> affectedParts,
                        Instant targetDate,
                        Severity severity,
                        String edcNotificationId,
                        LocalDateTime created,
                        LocalDateTime updated,
                        String messageId,
                        Boolean isInitial) {
        this.id = id;
        this.notificationReferenceId = notificationReferenceId;
        this.senderBpnNumber = senderBpnNumber;
        this.senderManufacturerName = senderManufacturerName;
        this.receiverBpnNumber = receiverBpnNumber;
        this.receiverManufacturerName = receiverManufacturerName;
        this.edcUrl = edcUrl;
        this.contractAgreementId = contractAgreementId;
        this.description = description;
        this.investigationStatus = investigationStatus;
        this.affectedParts = requireNonNullElseGet(affectedParts, ArrayList::new);
        this.targetDate = targetDate;
        this.severity = severity;
        this.edcNotificationId = edcNotificationId;
        this.created = created;
        this.updated = updated;
        this.messageId = messageId;
        this.isInitial = isInitial;
    }*/

    void changeStatusTo(InvestigationStatus to) {
        boolean transitionAllowed = investigationStatus.transitionAllowed(to);

        if (!transitionAllowed) {
            throw new NotificationStatusTransitionNotAllowed(id, investigationStatus, to);
        }
        this.investigationStatus = to;
    }


    // Important - receiver and sender will be saved in switched order
    public Notification copyAndSwitchSenderAndReceiver(BPN applicationBpn) {
        final String notificationId = UUID.randomUUID().toString();
        String receiverBPN = receiverBpnNumber;
        String senderBPN = senderBpnNumber;
        String receiverName;
        String senderName;

        // This is needed to make sure that the app can send a message to the receiver and not addresses itself
        if (applicationBpn.value().equals(receiverBpnNumber)) {
            receiverBPN = senderBpnNumber;
            senderBPN = receiverBpnNumber;
            receiverName = senderManufacturerName;
            senderName = receiverManufacturerName;
        } else {
            receiverName = receiverManufacturerName;
            senderName = senderManufacturerName;
        }
        return Notification.builder()
                .id(notificationId)
                .senderBpnNumber(senderBPN)
                .senderManufacturerName(senderName)
                .receiverBpnNumber(receiverBPN)
                .receiverManufacturerName(receiverName)
                .edcUrl(edcUrl)
                .contractAgreementId(contractAgreementId)
                .description(description)
                .investigationStatus(investigationStatus)
                .affectedParts(affectedParts)
                .targetDate(targetDate)
                .severity(severity)
                .edcNotificationId(edcNotificationId)
                .created(created)
                .updated(updated)
                .messageId(UUID.randomUUID().toString())
                .isInitial(false)
                .build();
    }
}
