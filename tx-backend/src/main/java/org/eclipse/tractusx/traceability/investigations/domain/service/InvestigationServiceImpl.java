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
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.investigations.application.request.CloseInvestigationRequest;
import org.eclipse.tractusx.traceability.investigations.application.request.UpdateInvestigationRequest;
import org.eclipse.tractusx.traceability.investigations.application.response.InvestigationData;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationId;
import org.eclipse.tractusx.traceability.investigations.domain.model.Severity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;


@Service
public class InvestigationServiceImpl implements InvestigationService {

    private final InvestigationsReadService investigationsReadService;
    private final InvestigationsPublisherService investigationsPublisherService;

    @Autowired
    public InvestigationServiceImpl(InvestigationsReadService investigationsReadService, InvestigationsPublisherService investigationsPublisherService) {
        this.investigationsReadService = investigationsReadService;
        this.investigationsPublisherService = investigationsPublisherService;
    }

    @Override
    public InvestigationId startInvestigation(BPN bpn, List<String> partIds, String description, Instant targetDate, String severity) {
        return investigationsPublisherService.startInvestigation(
                bpn, partIds, description, targetDate, Severity.fromString(severity));
    }

    @Override
    public PageResult<InvestigationData> getCreatedInvestigations(Pageable pageable) {
        return null;
    }

    @Override
    public PageResult<InvestigationData> getReceivedInvestigations(Pageable pageable) {
        return null;
    }

    @Override
    public InvestigationData getInvestigation(Long investigationId) {
        return null;
    }

    @Override
    public void approveInvestigation(Long investigationId) {

    }

    @Override
    public void cancelInvestigation(Long investigationId) {

    }

    @Override
    public void closeInvestigation(Long investigationId, CloseInvestigationRequest closeInvestigationRequest) {

    }

    @Override
    public void updateInvestigation(Long investigationId, UpdateInvestigationRequest updateInvestigationRequest) {

    }
}
