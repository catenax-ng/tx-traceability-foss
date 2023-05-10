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

package org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.jpa.asset.AssetEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.jpa.asset.JpaAssetsRepository;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.repository.InvestigationRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationAffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.base.QualityNotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.base.QualityNotificationStatusBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationNotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class InvestigationsRepositoryImpl implements InvestigationRepository {

    private final JpaInvestigationRepository jpaInvestigationRepository;

    private final JpaAssetsRepository assetsRepository;

    private final JpaNotificationRepository notificationRepository;

    private final Clock clock;

    @Override
    public void updateQualityNotificationMessageEntity(QualityNotificationMessage notification) {
        InvestigationNotificationEntity entity = notificationRepository.findById(notification.getId())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Notification with id %s not found!", notification.getId())));
        handleNotificationUpdate(entity, notification);

    }

    @Override
    public QualityNotificationId updateQualityNotificationEntity(QualityNotification investigation) {
        InvestigationEntity investigationEntity = jpaInvestigationRepository.findById(investigation.getInvestigationId().value())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Investigation with id %s not found!", investigation.getInvestigationId().value())));

        investigationEntity.setStatus(QualityNotificationStatusBaseEntity.fromStringValue(investigation.getInvestigationStatus().name()));
        investigationEntity.setUpdated(clock.instant());
        investigationEntity.setCloseReason(investigation.getCloseReason());
        investigationEntity.setAcceptReason(investigation.getAcceptReason());
        investigationEntity.setDeclineReason(investigation.getDeclineReason());

        handleNotificationUpdate(investigationEntity, investigation);
        jpaInvestigationRepository.save(investigationEntity);

        return investigation.getInvestigationId();
    }

    @Override
    public QualityNotificationId saveQualityNotificationEntity(QualityNotification investigation) {

        List<AssetEntity> assetEntities = getAssetEntitiesByInvestigation(investigation);

        if (!assetEntities.isEmpty()) {
            InvestigationEntity investigationEntity = InvestigationEntity.builder()
                    .assets(assetEntities)
                    .bpn(investigation.getBpn())
                    .description(investigation.getDescription())
                    .status(QualityNotificationStatusBaseEntity.fromStringValue(investigation.getInvestigationStatus().name()))
                    .side(QualityNotificationSideBaseEntity.valueOf(investigation.getInvestigationSide().name()))
                    .created(investigation.getCreatedAt())
                    .build();

            jpaInvestigationRepository.save(investigationEntity);

            investigation.getNotifications()
                    .forEach(notification -> handleNotificationCreate(investigationEntity, notification, assetEntities));

            return new QualityNotificationId(investigationEntity.getId());
        } else {
            throw new IllegalArgumentException("No assets found for %s asset ids".formatted(String.join(", ", investigation.getAssetIds())));
        }
    }

    @Override
    public PageResult<QualityNotification> findQualityNotificationsBySide(QualityNotificationSide investigationSide, Pageable pageable) {
        Page<InvestigationEntity> entities = jpaInvestigationRepository.findAllBySideEqualsOrderByCreatedDesc(QualityNotificationSideBaseEntity.valueOf(investigationSide.name()), pageable);
        return new PageResult<>(entities, this::toInvestigation);
    }

    @Override
    public Optional<QualityNotification> findOptionalQualityNotificationById(QualityNotificationId investigationId) {
        return jpaInvestigationRepository.findById(investigationId.value())
                .map(this::toInvestigation);
    }

    @Override
    public long countQualityNotificationEntitiesByStatus(QualityNotificationStatus qualityNotificationStatus) {
        return jpaInvestigationRepository.countAllByStatusEquals(QualityNotificationStatusBaseEntity.valueOf(qualityNotificationStatus.name()));
    }

    @Override
    public Optional<QualityNotification> findByEdcNotificationId(String edcNotificationId) {
        return jpaInvestigationRepository.findByNotificationsEdcNotificationId(edcNotificationId)
                .map(this::toInvestigation);
    }

    @Override
    public long countQualityNotificationEntitiesBySide(QualityNotificationSide investigationSide) {
        return jpaInvestigationRepository.countAllBySideEquals(QualityNotificationSideBaseEntity.valueOf(investigationSide.name()));
    }

    private void handleNotificationUpdate(InvestigationEntity investigationEntity, QualityNotification investigation) {

        List<InvestigationNotificationEntity> notificationEntities = new ArrayList<>(investigationEntity.getNotifications());
        Map<String, InvestigationNotificationEntity> notificationEntityMap = notificationEntities.stream().collect(Collectors.toMap(InvestigationNotificationEntity::getId, notificationEntity -> notificationEntity));
        for (QualityNotificationMessage notification : investigation.getNotifications()) {
            if (notificationExists(investigationEntity, notification.getId())) {
                log.info("handleNotificationUpdate::notificationExists with id {} for investigation with id {}", notification.getId(), investigation.getInvestigationId());
                handleNotificationUpdate(notificationEntityMap.get(notification.getId()), notification);
            } else {
                log.info("handleNotificationUpdate::new notification with id {} for investigation with id {}", notification.getId(), investigation.getInvestigationId());
                List<AssetEntity> assetEntitiesByInvestigation = getAssetEntitiesByInvestigation(investigation);
                handleNotificationCreate(investigationEntity, notification, assetEntitiesByInvestigation);
            }
        }

    }

    private List<AssetEntity> getAssetEntitiesByInvestigation(QualityNotification investigation) {
        return assetsRepository.findByIdIn(investigation.getAssetIds());
    }

    private void handleNotificationCreate(InvestigationEntity investigationEntity, QualityNotificationMessage notificationDomain, List<AssetEntity> assetEntities) {
        InvestigationNotificationEntity notificationEntity = toNotificationEntity(investigationEntity, notificationDomain, assetEntities);
        InvestigationNotificationEntity savedEntity = notificationRepository.save(notificationEntity);
        log.info("Successfully persisted notification entity {}", savedEntity);
    }

    private boolean notificationExists(InvestigationEntity investigationEntity, String notificationId) {
        List<InvestigationNotificationEntity> notificationEntities = new ArrayList<>(investigationEntity.getNotifications());
        return notificationEntities.stream().anyMatch(notification -> notification.getId().equals(notificationId));
    }

    private void handleNotificationUpdate(InvestigationNotificationEntity notificationEntity, QualityNotificationMessage notification) {
        notificationEntity.setEdcUrl(notification.getEdcUrl());
        notificationEntity.setContractAgreementId(notification.getContractAgreementId());
        notificationEntity.setNotificationReferenceId(notification.getNotificationReferenceId());
        notificationEntity.setTargetDate(notification.getTargetDate());
        notificationRepository.save(notificationEntity);
    }

    private QualityNotification toInvestigation(InvestigationEntity investigationEntity) {
        List<QualityNotificationMessage> notifications = investigationEntity.getNotifications().stream()
                .map(this::toNotification)
                .toList();

        List<String> assetIds = investigationEntity.getAssets().stream()
                .map(AssetEntity::getId)
                .toList();

        return QualityNotification.builder()
                .investigationId(new QualityNotificationId(investigationEntity.getId()))
                .bpn(BPN.of(investigationEntity.getBpn()))
                .investigationStatus(QualityNotificationStatus.fromStringValue(investigationEntity.getStatus().name()))
                .investigationSide(QualityNotificationSide.valueOf(investigationEntity.getSide().name()))
                .closeReason(investigationEntity.getCloseReason())
                .acceptReason(investigationEntity.getAcceptReason())
                .declineReason(investigationEntity.getDeclineReason())
                .createdAt(investigationEntity.getCreated())
                .description(investigationEntity.getDescription())
                .assetIds(assetIds)
                .notifications(notifications)
                .build();
    }

    public QualityNotificationMessage toNotification(InvestigationNotificationEntity notificationEntity) {
        InvestigationEntity investigation = notificationEntity.getInvestigation();

        return QualityNotificationMessage.builder()
                .id(notificationEntity.getId())
                .notificationReferenceId(notificationEntity.getNotificationReferenceId())
                .senderBpnNumber(notificationEntity.getSenderBpnNumber())
                .senderManufacturerName(notificationEntity.getSenderManufacturerName())
                .receiverBpnNumber(notificationEntity.getReceiverBpnNumber())
                .receiverManufacturerName(notificationEntity.getReceiverManufacturerName())
                .description(investigation.getDescription())
                .edcUrl(notificationEntity.getEdcUrl())
                .contractAgreementId(notificationEntity.getContractAgreementId())
                .investigationStatus(QualityNotificationStatus.fromStringValue(notificationEntity.getStatus().name()))
                .affectedParts(notificationEntity.getAssets().stream()
                        .map(asset -> new QualityNotificationAffectedPart(asset.getId()))
                        .toList())
                .targetDate(notificationEntity.getTargetDate())
                .severity(notificationEntity.getSeverity())
                .edcNotificationId(notificationEntity.getEdcNotificationId())
                .messageId(notificationEntity.getMessageId())
                .created(notificationEntity.getCreated())
                .updated(notificationEntity.getUpdated())
                .isInitial(notificationEntity.getIsInitial())
                .build();
    }


    private InvestigationNotificationEntity toNotificationEntity(InvestigationEntity investigationEntity, QualityNotificationMessage notification, List<AssetEntity> investigationAssets) {
        List<AssetEntity> notificationAssets = filterNotificationAssets(notification, investigationAssets);

        if (notificationAssets.isEmpty()) {
            throw new IllegalStateException("Investigation with id %s has no notification assets".formatted(investigationEntity.getId()));
        }

        return InvestigationNotificationEntity
                .builder()
                .id(notification.getId())
                .investigation(investigationEntity)
                .created(notification.getCreated())
                .senderBpnNumber(notification.getSenderBpnNumber())
                .senderManufacturerName(notification.getSenderManufacturerName())
                .receiverBpnNumber(notification.getReceiverBpnNumber())
                .receiverManufacturerName(notification.getReceiverManufacturerName())
                .assets(notificationAssets)
                .notificationReferenceId(notification.getNotificationReferenceId())
                .targetDate(notification.getTargetDate())
                .severity(notification.getSeverity())
                .edcNotificationId(notification.getEdcNotificationId())
                .status(QualityNotificationStatusBaseEntity.fromStringValue(notification.getInvestigationStatus().name()))
                .messageId(notification.getMessageId())
                .isInitial(notification.getIsInitial())
                .build();
    }

    private List<AssetEntity> filterNotificationAssets(QualityNotificationMessage notification, List<AssetEntity> assets) {
        Set<String> notificationAffectedAssetIds = notification.getAffectedParts().stream()
                .map(QualityNotificationAffectedPart::assetId)
                .collect(Collectors.toSet());

        return assets.stream()
                .filter(it -> notificationAffectedAssetIds.contains(it.getId()))
                .toList();
    }
}
