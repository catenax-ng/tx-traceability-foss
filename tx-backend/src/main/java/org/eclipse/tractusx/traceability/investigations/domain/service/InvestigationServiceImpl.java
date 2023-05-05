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

package org.eclipse.tractusx.traceability.investigations.domain.service;

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.investigations.application.response.InvestigationData;
import org.eclipse.tractusx.traceability.investigations.domain.model.Investigation;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationId;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationSide;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus;
import org.eclipse.tractusx.traceability.investigations.domain.model.Severity;
import org.eclipse.tractusx.traceability.investigations.domain.model.exception.InvestigationNotFoundException;
import org.eclipse.tractusx.traceability.investigations.domain.repository.InvestigationsRepository;
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
    public PageResult<InvestigationData> getCreatedInvestigations(Pageable pageable) {
        return getInvestigationsPageResult(pageable, InvestigationSide.SENDER);
    }

    @Override
    public PageResult<InvestigationData> getReceivedInvestigations(Pageable pageable) {
        return getInvestigationsPageResult(pageable, InvestigationSide.RECEIVER);
    }

    @Override
    public InvestigationData findInvestigation(Long id) {
        InvestigationId investigationId = new InvestigationId(id);
        Investigation investigation = loadInvestigationOrNotFoundException(investigationId);
        return investigation.toData();
    }

    @Override
    public Investigation loadInvestigationOrNotFoundException(InvestigationId investigationId) {
        return investigationsRepository.findById(investigationId)
                .orElseThrow(() -> new InvestigationNotFoundException(investigationId));
    }

    @Override
    public Investigation loadInvestigationByEdcNotificationIdOrNotFoundException(String edcNotificationId) {
        return investigationsRepository.findByEdcNotificationId(edcNotificationId)
                .orElseThrow(() -> new InvestigationNotFoundException(edcNotificationId));
    }

    @Override
    public void approveInvestigation(Long investigationId) {
        Investigation investigation = loadInvestigationOrNotFoundException(new InvestigationId(investigationId));
        investigationsPublisherService.approveInvestigation(investigation);
    }

    @Override
    public void cancelInvestigation(Long investigationId) {
        Investigation investigation = loadInvestigationOrNotFoundException(new InvestigationId(investigationId));
        investigationsPublisherService.cancelInvestigation(investigation);
    }

    @Override
    public void updateInvestigation(Long investigationId, InvestigationStatus status, String reason) {
        Investigation investigation = loadInvestigationOrNotFoundException(new InvestigationId(investigationId));
        investigationsPublisherService.updateInvestigationPublisher(investigation, status, reason);
    }

    private PageResult<InvestigationData> getInvestigationsPageResult(Pageable pageable, InvestigationSide investigationSide) {
        List<InvestigationData> investigationData = investigationsRepository.getInvestigations(investigationSide, pageable)
                .content()
                .stream()
                .sorted(Investigation.COMPARE_BY_NEWEST_INVESTIGATION_CREATION_TIME)
                .map(Investigation::toData)
                .toList();

        Page<InvestigationData> investigationDataPage = new PageImpl<>(investigationData, pageable, investigationsRepository.countInvestigations(investigationSide));

        return new PageResult<>(investigationDataPage);
    }
}
