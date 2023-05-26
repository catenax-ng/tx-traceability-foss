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

package org.eclipse.tractusx.traceability.assets.infrastructure.repository.jpa;

import org.eclipse.tractusx.traceability.assets.domain.exception.AssetNotFoundException;
import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.domain.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.service.repository.AssetRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.model.AssetEntity;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class PersistentAssetsRepository implements AssetRepository {

    private final JpaAssetsRepository assetsRepository;

    public PersistentAssetsRepository(JpaAssetsRepository assetsRepository) {
        this.assetsRepository = assetsRepository;
    }

    @Override
    @Transactional
    public Asset getAssetById(String assetId) {
        return assetsRepository.findById(assetId)
                .map(AssetEntity::toDomain)
                .orElseThrow(() -> new AssetNotFoundException("Asset with id %s was not found.".formatted(assetId)));
    }

    @Override
    public List<Asset> getAssetsById(List<String> assetIds) {
        return assetsRepository.findByIdIn(assetIds).stream()
                .map(AssetEntity::toDomain)
                .toList();
    }

    @Override
    public Asset getAssetByChildId(String assetId, String childId) {
        return assetsRepository.findById(childId)
                .map(AssetEntity::toDomain)
                .orElseThrow(() -> new AssetNotFoundException("Child Asset Not Found"));
    }

    @Override
    public PageResult<Asset> getAssets(Pageable pageable, Owner owner) {
        if (owner != null) {
            return new PageResult<>(assetsRepository.findByOwner(pageable, owner), AssetEntity::toDomain);
        }
        return new PageResult<>(assetsRepository.findAll(pageable), AssetEntity::toDomain);
    }

    @Override
    @Transactional
    public List<Asset> getAssets() {
        return AssetEntity.toDomainList(assetsRepository.findAll());
    }

    @Override
    public Asset save(Asset asset) {
        return AssetEntity.toDomain(assetsRepository.save(AssetEntity.from(asset)));
    }

    @Override
    @Transactional
    public List<Asset> saveAll(List<Asset> assets) {
        return AssetEntity.toDomainList(assetsRepository.saveAll(AssetEntity.fromList(assets)));
    }

    @Transactional
    public List<Asset> updateAllParentDescriptionsAndOwner(final List<Asset> assetList) {
        List<Asset> saved = new ArrayList<>();
        for (Asset asset : assetList) {
            saved.add(updateParentDescriptionsAndOwner(asset.getId(), asset.getParentDescriptions(), asset.getOwner()));
        }
        return saved;
    }

    @Transactional
    public Asset updateParentDescriptionsAndOwner(final String assetId, List<Descriptions> parentDescriptions, Owner owner) {
        Asset assetById = this.getAssetById(assetId);
        assetById.setParentDescriptions(parentDescriptions);
        assetById.setOwner(owner);
        return save(assetById);
    }

    @Override
    public long countAssets() {
        return assetsRepository.count();
    }

    @Override
    public long countAssetsByOwner(Owner owner) {
        return assetsRepository.countAssetsByOwner(owner);
    }

}
