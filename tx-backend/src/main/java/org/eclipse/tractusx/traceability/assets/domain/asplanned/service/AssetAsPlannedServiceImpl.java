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

package org.eclipse.tractusx.traceability.assets.domain.asplanned.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.repository.AssetAsPlannedRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.AssetRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.IrsRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.service.AbstractAssetBaseService;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssetAsPlannedServiceImpl extends AbstractAssetBaseService {

    private final AssetAsPlannedRepository assetAsPlannedRepository;

    private final IrsRepository irsRepository;

    @Override
    protected AssetRepository getAssetRepository() {
        return assetAsPlannedRepository;
    }

    @Override
    protected List<String> getDownwardAspects() {
        return Aspect.downwardAspectsForAssetsAsPlanned();
    }

    @Override
    protected List<String> getUpwardAspects() {
        return Collections.emptyList();
    }

    @Override
    protected BomLifecycle getBomLifecycle() {
        return BomLifecycle.AS_PLANNED;
    }

    @Override
    protected IrsRepository getIrsRepository() {
        return irsRepository;
    }
}
