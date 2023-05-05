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

import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.investigations.application.response.InvestigationData;
import org.eclipse.tractusx.traceability.investigations.domain.model.Investigation;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationId;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

public interface InvestigationService {
    InvestigationId startInvestigation(List<String> partIds, String description, Instant targetDate, String severity);

    PageResult<InvestigationData> getCreatedInvestigations(Pageable pageable);

    PageResult<InvestigationData> getReceivedInvestigations(Pageable pageable);

    InvestigationData findInvestigation(Long investigationId);

    Investigation loadInvestigationOrNotFoundException(InvestigationId investigationId);

    Investigation loadInvestigationByEdcNotificationIdOrNotFoundException(String edcNotificationId);

    void approveInvestigation(Long investigationId);

    void cancelInvestigation(Long investigationId);

    void closeInvestigation(Long investigationId, InvestigationStatus investigationStatus, String reason);

    void updateInvestigation(Long investigationId, InvestigationStatus investigationStatus, String reason);
}
