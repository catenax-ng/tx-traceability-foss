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
package org.eclipse.tractusx.traceability.assets.domain.importpoc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.GenericSubmodel;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Submodel;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.SemanticDataModel;

import java.util.List;

public record AssetWrapperRequest(AssetMetaInfoRequest assetMetaInfoRequest,
                                  List<SemanticDataModel> mainAspectModels,
                                  List<GenericSubmodel> upwardRelationship,
                                  List<GenericSubmodel> downwardRelationship,
                                  BomLifecycle bomLifecycle
) {

    @JsonCreator
    static AssetWrapperRequest of(
            @JsonProperty("assetMetaInfo") AssetMetaInfoRequest assetMetaInfoRequest,
            @JsonProperty("submodels") List<GenericSubmodel> submodels
    ) {
        List<GenericSubmodel> upwardSubmodels = submodels.stream().filter(submodel -> isUpwardRelationship(submodel.getAspectType())).toList();
        List<GenericSubmodel> downwardSubmodels = submodels.stream().filter(submodel -> isDownwardRelationship(submodel.getAspectType())).toList();
        List<GenericSubmodel> mainAspectSubmodels = submodels.stream().filter(submodel -> isMainAspect(submodel.getAspectType())).toList();
        List<SemanticDataModel> mainAspectSemanticDataModel = transformMainAspectModel(mainAspectSubmodels);
       BomLifecycle bom = mainAspectSubmodels.stream().findAny()
               .map(submodel -> submodel.getAspectType().contains("AsPlanned") ? BomLifecycle.AS_PLANNED : BomLifecycle.AS_BUILT).orElseThrow();

        return new AssetWrapperRequest(assetMetaInfoRequest, mainAspectSemanticDataModel, upwardSubmodels, downwardSubmodels, bom);
    }

    private static List<SemanticDataModel> transformMainAspectModel(List<GenericSubmodel> submodels) {
        return submodels.stream()
                .map(submodel -> (SemanticDataModel) submodel.getPayload()).toList();
    }


    private static boolean isUpwardRelationship(final String aspectType) {
        return aspectType.contains("Bom");
    }

    private static boolean isDownwardRelationship(final String aspectType) {
        return aspectType.contains("Usage");
    }

    private static boolean isMainAspect(final String aspectType) {
        return !isDownwardRelationship(aspectType) && !isUpwardRelationship(aspectType);
    }
}
