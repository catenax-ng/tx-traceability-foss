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

package org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.service;

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.qualitynotification.application.investigation.response.InvestigationResponse;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.Severity;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationNotFoundException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.repository.InvestigationsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class InvestigationServiceImpl implements InvestigationService {

    private final InvestigationsPublisherService investigationsPublisherService;

    private final InvestigationsRepository investigationsRepository;


    @Override
    public InvestigationId startInvestigation(List<String> partIds, String description, Instant targetDate, String severity) {
        return investigationsPublisherService.startInvestigation(partIds, description, targetDate, Severity.fromString(severity));
    }

    @Override
    public PageResult<InvestigationResponse> getCreatedInvestigations(Pageable pageable) {
        return getInvestigationsPageResult(pageable, QualityNotificationSide.SENDER);
    }

    @Override
    public PageResult<InvestigationResponse> getReceivedInvestigations(Pageable pageable) {
        return getInvestigationsPageResult(pageable, QualityNotificationSide.RECEIVER);
    }

    @Override
    public InvestigationResponse findInvestigation(Long id) {
        InvestigationId investigationId = new InvestigationId(id);
        QualityNotification investigation = loadInvestigationOrNotFoundException(investigationId);
        return investigation.toDTO();
    }

    @Override
    public QualityNotification loadInvestigationOrNotFoundException(InvestigationId investigationId) {
        return investigationsRepository.findOptionalQualityNotificationById(investigationId)
                .orElseThrow(() -> new InvestigationNotFoundException(investigationId));
    }

    @Override
    public QualityNotification loadInvestigationByEdcNotificationIdOrNotFoundException(String edcNotificationId) {
        return investigationsRepository.findByEdcNotificationId(edcNotificationId)
                .orElseThrow(() -> new InvestigationNotFoundException(edcNotificationId));
    }

    @Override
    public void approveInvestigation(Long investigationId) {
        QualityNotification investigation = loadInvestigationOrNotFoundException(new InvestigationId(investigationId));
        investigationsPublisherService.approveInvestigation(investigation);
    }

    @Override
    public void cancelInvestigation(Long investigationId) {
        QualityNotification investigation = loadInvestigationOrNotFoundException(new InvestigationId(investigationId));
        investigationsPublisherService.cancelInvestigation(investigation);
    }

    @Override
    public void updateInvestigation(Long investigationId, QualityNotificationStatus status, String reason) {
        QualityNotification investigation = loadInvestigationOrNotFoundException(new InvestigationId(investigationId));
        investigationsPublisherService.updateInvestigationPublisher(investigation, status, reason);
    }

    private PageResult<InvestigationResponse> getInvestigationsPageResult(Pageable pageable, QualityNotificationSide investigationSide) {
        List<InvestigationResponse> investigationData = investigationsRepository.findQualityNotificationsBySide(investigationSide, pageable)
                .content()
                .stream()
                .sorted(QualityNotification.COMPARE_BY_NEWEST_INVESTIGATION_CREATION_TIME)
                .map(QualityNotification::toDTO)
                .toList();

        Page<InvestigationResponse> investigationDataPage = new PageImpl<>(investigationData, pageable, investigationsRepository.countQualityNotificationEntitiesBySide(investigationSide));

        return new PageResult<>(investigationDataPage);
    }
}
