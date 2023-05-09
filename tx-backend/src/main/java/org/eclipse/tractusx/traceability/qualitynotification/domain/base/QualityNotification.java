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
package org.eclipse.tractusx.traceability.qualitynotification.domain.base;

import lombok.Builder;
import lombok.Data;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.qualitynotification.application.investigation.response.InvestigationReason;
import org.eclipse.tractusx.traceability.qualitynotification.application.investigation.response.InvestigationResponse;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.AffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.Severity;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationIllegalUpdate;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationStatusTransitionNotAllowed;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

@Data
@Builder
public class QualityNotification {
    public static final Comparator<QualityNotification> COMPARE_BY_NEWEST_INVESTIGATION_CREATION_TIME = (o1, o2) -> {
        Instant o1CreationTime = o1.createdAt;
        Instant o2CreationTime = o2.createdAt;

        if (o1CreationTime.equals(o2CreationTime)) {
            return 0;
        }

        if (o1CreationTime.isBefore(o2CreationTime)) {
            return 1;
        }

        return -1;
    };
    private BPN bpn;
    private InvestigationId investigationId;
    private QualityNotificationStatus investigationStatus;
    private String description;
    private Instant createdAt;
    private QualityNotificationSide investigationSide;
    @Builder.Default
    private List<String> assetIds = new ArrayList<>();
    private String closeReason;
    private String acceptReason;
    private String declineReason;
    private Map<String, QualityNotificationMessage> notifications = new HashMap<>();

    public static QualityNotification startInvestigation(Instant createDate, BPN bpn, String description) {
        return QualityNotification.builder()
                .bpn(bpn)
                .investigationStatus(QualityNotificationStatus.CREATED)
                .investigationSide(QualityNotificationSide.SENDER)
                .description(description)
                .createdAt(createDate)
                .assetIds(Collections.emptyList())
                .notifications(Collections.emptyList())
                .build();


    }

    private static String getSenderBPN(Collection<QualityNotificationMessage> notifications) {
        return notifications.stream()
                .findFirst()
                .map(QualityNotificationMessage::getSenderBpnNumber)
                .orElse(null);
    }

    private static String getReceiverBPN(Collection<QualityNotificationMessage> notifications) {
        return notifications.stream()
                .findFirst()
                .map(QualityNotificationMessage::getReceiverBpnNumber)
                .orElse(null);
    }

    private static String getSenderName(Collection<QualityNotificationMessage> notifications) {
        return notifications.stream()
                .findFirst()
                .map(QualityNotificationMessage::getSenderManufacturerName)
                .orElse(null);
    }

    private static String getReceiverName(Collection<QualityNotificationMessage> notifications) {
        return notifications.stream()
                .findFirst()
                .map(QualityNotificationMessage::getReceiverManufacturerName)
                .orElse(null);
    }

    public List<String> getAssetIds() {
        return Collections.unmodifiableList(assetIds);
    }

    public InvestigationResponse toDTO() {
        return InvestigationResponse
                .builder()
                .id(investigationId.value())
                .status(investigationStatus.name())
                .description(description)
                .createdBy(getSenderBPN(notifications.values()))
                .createdByName(getSenderName(notifications.values()))
                .createdDate(createdAt.toString())
                .assetIds(Collections.unmodifiableList(assetIds))
                .channel(investigationSide)
                .reason(new InvestigationReason(
                        closeReason,
                        acceptReason,
                        declineReason
                ))
                .sendTo(getReceiverBPN(notifications.values()))
                .sendToName(getReceiverName(notifications.values()))
                .severity(notifications.entrySet().stream().findFirst().map(Map.Entry::getValue).map(QualityNotificationMessage::getSeverity).orElse(Severity.MINOR).getRealName())
                .targetDate(notifications.entrySet().stream().findFirst().map(Map.Entry::getValue).map(QualityNotificationMessage::getTargetDate).map(Instant::toString).orElse(null)).build();
    }

    public String getBpn() {
        return bpn.value();
    }

    public void cancel(BPN applicationBpn) {
        validateBPN(applicationBpn);
        changeStatusTo(QualityNotificationStatus.CANCELED);
        this.closeReason = "canceled";
    }

    public void close(BPN applicationBpn, String reason) {
        validateBPN(applicationBpn);
        changeStatusTo(QualityNotificationStatus.CLOSED);
        this.closeReason = reason;
        if (this.notifications != null) {
            this.notifications.values()
                    .forEach(notification -> notification.setDescription(reason));
        }

    }

    public void acknowledge(QualityNotificationMessage notification) {
        changeStatusToWithoutNotifications(QualityNotificationStatus.ACKNOWLEDGED);
        setInvestigationStatusAndReasonForNotification(notification, QualityNotificationStatus.ACKNOWLEDGED, null);
        notification.setInvestigationStatus(QualityNotificationStatus.ACKNOWLEDGED);
    }

    public void accept(String reason, QualityNotificationMessage notification) {
        changeStatusToWithoutNotifications(QualityNotificationStatus.ACCEPTED);
        this.acceptReason = reason;
        setInvestigationStatusAndReasonForNotification(notification, QualityNotificationStatus.ACCEPTED, reason);
        notification.setInvestigationStatus(QualityNotificationStatus.ACCEPTED);
        notification.setDescription(reason);
    }

    public void decline(String reason, QualityNotificationMessage notification) {
        changeStatusToWithoutNotifications(QualityNotificationStatus.DECLINED);
        this.declineReason = reason;
        setInvestigationStatusAndReasonForNotification(notification, QualityNotificationStatus.DECLINED, reason);
        notification.setInvestigationStatus(QualityNotificationStatus.DECLINED);
        notification.setDescription(reason);
    }

    public void close(String reason, QualityNotificationMessage notification) {
        changeStatusToWithoutNotifications(QualityNotificationStatus.CLOSED);
        this.closeReason = reason;
        setInvestigationStatusAndReasonForNotification(notification, QualityNotificationStatus.CLOSED, reason);
        notification.setInvestigationStatus(QualityNotificationStatus.CLOSED);
        notification.setDescription(reason);
    }

    public void send(BPN applicationBpn) {
        validateBPN(applicationBpn);
        changeStatusTo(QualityNotificationStatus.SENT);
    }

    private void setInvestigationStatusAndReasonForNotification(QualityNotificationMessage notificationDomain, QualityNotificationStatus investigationStatus, String reason) {
        if (this.notifications != null) {
            for (QualityNotificationMessage notification : this.notifications.values()) {
                if (notification.getId().equals(notificationDomain.getId())) {
                    if (reason != null) {
                        notification.setDescription(reason);
                    }
                    notification.setInvestigationStatus(investigationStatus);
                    break;
                }
            }
        }
    }

    private void validateBPN(BPN applicationBpn) {
        if (!applicationBpn.equals(this.bpn)) {
            throw new InvestigationIllegalUpdate("%s bpn has no permissions to update investigation with %s id.".formatted(applicationBpn.value(), investigationId.value()));
        }
    }

    private void changeStatusTo(QualityNotificationStatus to) {
        boolean transitionAllowed = investigationStatus.transitionAllowed(to);

        if (!transitionAllowed) {
            throw new InvestigationStatusTransitionNotAllowed(investigationId, investigationStatus, to);
        }

        if (notifications != null) {
            notifications.values()
                    .forEach(notification -> notification.changeStatusTo(to));
        }

        this.investigationStatus = to;
    }

    private void changeStatusToWithoutNotifications(QualityNotificationStatus to) {
        boolean transitionAllowed = investigationStatus.transitionAllowed(to);

        if (!transitionAllowed) {
            throw new InvestigationStatusTransitionNotAllowed(investigationId, investigationStatus, to);
        }

        this.investigationStatus = to;
    }

    public List<QualityNotificationMessage> getNotifications() {
        return new ArrayList<>(notifications.values());
    }

    public void addNotification(QualityNotificationMessage notification) {
        notifications.put(notification.getId(), notification);

        List<String> newAssetIds = new ArrayList<>(assetIds); // create a mutable copy of assetIds
        notification.getAffectedParts().stream()
                .map(AffectedPart::assetId)
                .forEach(newAssetIds::add);

        assetIds = Collections.unmodifiableList(newAssetIds); //
    }

    public static class QualityNotificationBuilder {
        public QualityNotificationBuilder notifications(List<QualityNotificationMessage> notifications) {
            this.notifications = emptyIfNull(notifications).stream()
                    .collect(Collectors.toMap(QualityNotificationMessage::getId, Function.identity()));
            return this;
        }
    }

}
