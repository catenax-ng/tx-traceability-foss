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

package org.eclipse.tractusx.traceability.assets.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.domain.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.model.QualityType;
import org.eclipse.tractusx.traceability.assets.domain.service.repository.AssetRepository;
import org.eclipse.tractusx.traceability.assets.domain.service.repository.IrsRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.config.async.AssetsAsyncConfig;
import org.eclipse.tractusx.traceability.assets.infrastructure.repository.rest.irs.model.Aspect;
import org.eclipse.tractusx.traceability.assets.infrastructure.repository.rest.irs.model.Direction;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

@Slf4j
@Component
public class AssetService {

    private final AssetRepository assetRepository;
    private final IrsRepository irsRepository;
    private final TraceabilityProperties traceabilityProperties;

    public AssetService(AssetRepository assetRepository, IrsRepository irsRepository, TraceabilityProperties traceabilityProperties) {
        this.assetRepository = assetRepository;
        this.irsRepository = irsRepository;
        this.traceabilityProperties = traceabilityProperties;
    }

    @Async(value = AssetsAsyncConfig.SYNCHRONIZE_ASSETS_EXECUTOR)
    public void synchronizeAssetsAsync(List<String> globalAssetIds) {
        for (String globalAssetId : globalAssetIds) {
            try {
                synchronizeAssetsAsync(globalAssetId);
            } catch (Exception e) {
                log.warn("Cannot fetch assets for id: {}. Error: {}", globalAssetId, e.getMessage());
            }
        }
    }

    @Async(value = AssetsAsyncConfig.SYNCHRONIZE_ASSETS_EXECUTOR)
    public void synchronizeAssetsAsync(String globalAssetId) {
        log.info("Synchronizing assets for globalAssetId: {}", globalAssetId);
        try {
            List<Asset> downwardAssets = irsRepository.findAssets(globalAssetId, Direction.DOWNWARD, Aspect.downwardAspects());
            List<Asset> syncedDownwardAssets = downwardAssets.stream().filter(asset -> asset.getId().equals(globalAssetId)).toList();
            assetRepository.saveAll(syncedDownwardAssets);

         /*   log.info("###########Downward Asset loop start with size {} for globalAssetId {}", downwardAssets.size(), globalAssetId);
            downwardAssets.forEach(asset -> {
                log.info("Asset with id {} has parents {} with id {} has childs {} with id {}", asset.getId(), asset.getParentDescriptions().size(), asset.getParentDescriptions(), asset.getChildDescriptions().size(), asset.getChildDescriptions());
            });
            log.info("###########Downward Asset loop ended!");*/
            List<Asset> upwardAssets = irsRepository.findAssets(globalAssetId, Direction.UPWARD, Aspect.upwardAspects());
            List<Asset> syncedUpwardAssets = upwardAssets.stream().filter(asset -> asset.getId().equals(globalAssetId)).toList();
            assetRepository.updateOrCreateParentDescriptionsIncludingOwner(syncedUpwardAssets);
          /*  log.info("$$$$$$$$$$$Upward Asset loop start with size {} for globalAssetId {}", upwardAssets.size(), globalAssetId);
            upwardAssets.forEach(asset -> {
                log.info("Asset with id {} has parents {} with id {} has childs {} with id {}", asset.getId(), asset.getParentDescriptions().size(), asset.getParentDescriptions(), asset.getChildDescriptions().size(), asset.getChildDescriptions());
            });
            log.info("$$$$$$$$$$$Downward Asset loop ended!");*/

          /*  List<Asset> combinedAssetList = combineAssetsAndMergeParentDescriptionIntoDownwardAssets(downwardAssets, upwardAssets);

            log.info("Assets {} for globalAssetId {} successfully saved.", combinedAssetList, globalAssetId);*/


            List<Asset> unsyncedDownwardAssets = downwardAssets.stream().filter(asset -> !asset.getId().equals(globalAssetId)).toList();
            List<Asset> unsyncedUpwardAssets = upwardAssets.stream().filter(asset -> !asset.getId().equals(globalAssetId)).toList();
            List<Asset> unsyncedAssets = new ArrayList<>(unsyncedDownwardAssets);
            unsyncedAssets.addAll(unsyncedUpwardAssets);
            unsyncedAssets.forEach(asset -> synchronizeAssetsAsync(asset.getId()));

        } catch (Exception e) {
            log.warn("Exception during assets synchronization for globalAssetId: {}. Message: {}.", globalAssetId, e.getMessage(), e);
        }
    }

    /**
     * Combines the list of downward assets with the list of upward assets by merging the parent descriptions of
     * matching child assets into the corresponding downward assets. If an upward asset has no matching downward asset,
     * it is added to the result list as is.
     *
     * @param downwardAssets the list of downward assets to be combined with the upward assets
     * @param upwardAssets   the list of upward assets to be combined with the downward assets
     * @return a new list of {@link Asset} objects that contains the combined assets with merged parent descriptions
     */
    public List<Asset> combineAssetsAndMergeParentDescriptionIntoDownwardAssets(List<Asset> downwardAssets, List<Asset> upwardAssets) {
        List<Asset> combinedList = new ArrayList<>(downwardAssets);

        Map<String, Asset> downwardAssetsMap = emptyIfNull(downwardAssets).stream()
                .collect(Collectors.toMap(Asset::getId, Function.identity()));

        for (Asset upwardAsset : upwardAssets) {
            if (downwardAssetsMap.get(upwardAsset.getId()) != null) {
                for (Asset byId : combinedList) {
                    if (byId.getId().equals(upwardAsset.getId())) {
                        byId.setParentDescriptions(upwardAsset.getParentDescriptions());
                        byId.setChildDescriptions(downwardAssetsMap.get(upwardAsset.getId()).getChildDescriptions());
                        if (byId.getOwner().equals(Owner.UNKNOWN)) {
                            if (traceabilityProperties.getBpn().value().equals(byId.getManufacturerId())) {
                                byId.setOwner(Owner.OWN);
                            }
                            for (Descriptions descriptions : byId.getParentDescriptions()) {
                                if (descriptions.id().contains(byId.getId())) {
                                    byId.setOwner(Owner.CUSTOMER);
                                }
                            }
                        }
                    }
                }
            } else {
                combinedList.add(upwardAsset);
            }
        }
        return combinedList;
    }

    public void setAssetsInvestigationStatus(QualityNotification investigation) {
        assetRepository.getAssetsById(investigation.getAssetIds()).forEach(asset -> {
            // Assets in status closed will be false, others true
            asset.setUnderInvestigation(!investigation.getInvestigationStatus().equals(QualityNotificationStatus.CLOSED));
            assetRepository.save(asset);
        });

    }

    public Asset updateQualityType(String assetId, QualityType qualityType) {
        Asset foundAsset = assetRepository.getAssetById(assetId);
        foundAsset.setQualityType(qualityType);
        return assetRepository.save(foundAsset);
    }

    public Map<String, Long> getAssetsCountryMap() {
        return assetRepository.getAssets().stream()
                .collect(Collectors.groupingBy(Asset::getManufacturingCountry, Collectors.counting()));
    }

    public void saveAssets(List<Asset> assets) {
        assetRepository.saveAll(assets);
    }

    public PageResult<Asset> getAssets(Pageable pageable, Owner owner) {
        return assetRepository.getAssets(pageable, owner);
    }

    public Asset getAssetById(String assetId) {
        return assetRepository.getAssetById(assetId);
    }

    public List<Asset> getAssetsById(List<String> assetIds) {
        return assetRepository.getAssetsById(assetIds);
    }

    public Asset getAssetByChildId(String assetId, String childId) {
        return assetRepository.getAssetByChildId(assetId, childId);
    }
}
