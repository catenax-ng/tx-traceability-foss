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
package org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.jpa.asset.AssetEntity;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.Severity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "notification")
public class NotificationEntity {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "investigation_id")
    private InvestigationEntity investigation;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "assets_notifications",
            joinColumns = @JoinColumn(name = "notification_id"),
            inverseJoinColumns = @JoinColumn(name = "asset_id")
    )
    private List<AssetEntity> assets;

    private String senderBpnNumber;

    private String senderManufacturerName;
    private String receiverBpnNumber;

    private String receiverManufacturerName;
    private String edcUrl;
    private String contractAgreementId;
    private String notificationReferenceId;
    private Instant targetDate;
    private Severity severity;
    private String edcNotificationId;
    private LocalDateTime created;
    private LocalDateTime updated;
    private InvestigationStatus status;
    private String messageId;
    private Boolean isInitial;

    public NotificationEntity(String id,
                              InvestigationEntity investigation,
                              String senderBpnNumber,
                              String senderManufacturerName,
                              String receiverBpnNumber,
                              String receiverManufacturerName,
                              List<AssetEntity> assets,
                              String notificationReferenceId,
                              Instant targetDate,
                              Severity severity,
                              String edcNotificationId,
                              InvestigationStatus status,
                              String messageId,
                              Boolean isInitial) {
        this.id = id;
        this.investigation = investigation;
        this.senderBpnNumber = senderBpnNumber;
        this.senderManufacturerName = senderManufacturerName;
        this.receiverBpnNumber = receiverBpnNumber;
        this.receiverManufacturerName = receiverManufacturerName;
        this.assets = assets;
        this.notificationReferenceId = notificationReferenceId;
        this.targetDate = targetDate;
        this.severity = severity;
        this.edcNotificationId = edcNotificationId;
        this.created = LocalDateTime.now();
        this.status = status;
        this.messageId = messageId;
        this.isInitial = isInitial;
    }

    @PreUpdate
    public void preUpdate() {
        this.updated = LocalDateTime.now();
    }

}
