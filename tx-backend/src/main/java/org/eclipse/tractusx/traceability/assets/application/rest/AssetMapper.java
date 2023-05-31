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

package org.eclipse.tractusx.traceability.assets.application.rest;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.eclipse.tractusx.traceability.assets.application.rest.response.AssetResponse;
import org.eclipse.tractusx.traceability.assets.application.rest.response.DescriptionsResponse;
import org.eclipse.tractusx.traceability.assets.application.rest.response.OwnerResponse;
import org.eclipse.tractusx.traceability.assets.application.rest.response.QualityTypeResponse;
import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.common.model.PageResult;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssetMapper {

    public static AssetResponse toResponse(final Asset asset) {
        return AssetResponse.builder()
                .id(asset.getId())
                .idShort(asset.getIdShort())
                .nameAtManufacturer(asset.getNameAtManufacturer())
                .manufacturerPartId(asset.getManufacturerPartId())
                .partInstanceId(asset.getPartInstanceId())
                .manufacturerId(asset.getManufacturerId())
                .batchId(asset.getBatchId())
                .manufacturerName(asset.getManufacturerName())
                .nameAtCustomer(asset.getNameAtCustomer())
                .customerPartId(asset.getCustomerPartId())
                .manufacturingDate(asset.getManufacturingDate())
                .manufacturingCountry(asset.getManufacturingCountry())
                .owner(OwnerResponse.from(asset.getOwner()))
                .childDescriptions(
                        asset.getChildDescriptions().stream()
                                .map(DescriptionsResponse::from)
                                .toList())
                .parentDescriptions(
                        asset.getParentDescriptions().stream()
                                .map(DescriptionsResponse::from)
                                .toList())
                .underInvestigation(asset.isUnderInvestigation())
                .qualityType(
                        QualityTypeResponse.from(asset.getQualityType())
                )
                .van(asset.getVan())
                .build();
    }

    public static PageResult<AssetResponse> toPageResultResponse(final PageResult<Asset> assetPageResult) {
        return new PageResult<>(
                assetPageResult.content().stream()
                        .map(AssetMapper::toResponse).toList(),
                assetPageResult.page(),
                assetPageResult.pageCount(),
                assetPageResult.pageSize(),
                assetPageResult.totalItems()
        );
    }

    public static List<AssetResponse> toResponseList(final List<Asset> assets) {
        return assets.stream()
                .map(AssetMapper::toResponse)
                .toList();
    }
}
