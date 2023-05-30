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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.eclipse.tractusx.traceability.assets.infrastructure.model.AssetEntity;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.QualityNotificationBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.QualityNotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.QualityNotificationStatusBaseEntity;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@SuperBuilder
@Table(name = "investigation")
public class InvestigationEntity extends QualityNotificationBaseEntity {

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "assets_investigations",
            joinColumns = @JoinColumn(name = "investigation_id"),
            inverseJoinColumns = @JoinColumn(name = "asset_id")
    )
    private List<AssetEntity> assets;

    @OneToMany(mappedBy = "investigation")
    private List<InvestigationNotificationEntity> notifications;

    public static QualityNotification toDomain(InvestigationEntity investigationNotificationEntity) {
        List<QualityNotificationMessage> notifications = investigationNotificationEntity.getNotifications().stream()
                .map(InvestigationNotificationEntity::toDomain)
                .toList();

        List<String> assetIds = investigationNotificationEntity.getAssets().stream()
                .map(AssetEntity::getId)
                .toList();

        return QualityNotification.builder()
                .investigationId(new QualityNotificationId(investigationNotificationEntity.getId()))
                .bpn(BPN.of(investigationNotificationEntity.getBpn()))
                .investigationStatus(QualityNotificationStatus.fromStringValue(investigationNotificationEntity.getStatus().name()))
                .investigationSide(QualityNotificationSide.valueOf(investigationNotificationEntity.getSide().name()))
                .closeReason(investigationNotificationEntity.getCloseReason())
                .acceptReason(investigationNotificationEntity.getAcceptReason())
                .declineReason(investigationNotificationEntity.getDeclineReason())
                .createdAt(investigationNotificationEntity.getCreatedDate())
                .description(investigationNotificationEntity.getDescription())
                .assetIds(assetIds)
                .notifications(notifications)
                .build();
    }

    public static InvestigationEntity from(QualityNotification qualityNotification, List<AssetEntity> assetEntities) {
        return InvestigationEntity.builder()
                .assets(assetEntities)
                .bpn(qualityNotification.getBpn())
                .description(qualityNotification.getDescription())
                .status(QualityNotificationStatusBaseEntity.fromStringValue(qualityNotification.getInvestigationStatus().name()))
                .side(QualityNotificationSideBaseEntity.valueOf(qualityNotification.getInvestigationSide().name()))
                .createdDate(qualityNotification.getCreatedAt())
                .build();
    }

}
