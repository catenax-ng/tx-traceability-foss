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

package org.eclipse.tractusx.traceability.assets.domain.service;

import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.model.QualityType;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.Owner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

    @InjectMocks
    private AssetService assetService;

    @Test
    void testMerge() {
        List<Asset.Descriptions> parentDescriptionsList = new ArrayList<>();
        parentDescriptionsList.add(new Asset.Descriptions("parentId", "idshort"));
        parentDescriptionsList.add(new Asset.Descriptions("parentId2", "idshort"));
        List<Asset.Descriptions> childDescriptionList = new ArrayList<>();
        childDescriptionList.add(new Asset.Descriptions("childId", "idshort"));
        childDescriptionList.add(new Asset.Descriptions("childId2", "idshort"));

        String id = "urn:uuid:ceb6b964-5779-49c1-b5e9-0ee70528fcbd";
        String idShort = "--";
        String nameAtManufacturer = "1";
        String nameAtManufacturer2 = "2";
        String manufacturerPartId = "33740332-54";
        String partInstanceId = "NO-297452866581906730261974";
        String manufacturerId = "BPNL00000003CSGV";
        String batchId = "--";
        String manufacturerName = "Tier C";
        String nameAtCustomer = "Door front-right";
        String customerPartId = "33740332-54";
        Instant manufacturingDate = Instant.parse("2022-02-04T13:48:54Z");
        String manufacturingCountry = "DEU";
        Owner owner = Owner.CUSTOMER;
        QualityType qualityType = QualityType.OK;
        String van = "--";

        Asset asset = new Asset(id, idShort, nameAtManufacturer, manufacturerPartId, partInstanceId, manufacturerId, batchId, manufacturerName, nameAtCustomer, customerPartId, manufacturingDate, manufacturingCountry, owner, childDescriptionList, Collections.emptyList(), false, qualityType, van);
        Asset asset2 = new Asset(id, idShort, nameAtManufacturer2, manufacturerPartId, partInstanceId, manufacturerId, batchId, manufacturerName, nameAtCustomer, customerPartId, manufacturingDate, manufacturingCountry, owner, Collections.emptyList(), parentDescriptionsList, false, qualityType, van);
        List<Asset> assets = assetService.mergeParentDescriptionsIntoDownWardAssetList(List.of(asset), List.of(asset2));
        assertThat(assets).hasSize(1);
        assertThat(assets.get(0).getChildDescriptions()).hasSize(2);
        assertThat(assets.get(0).getParentDescriptions()).hasSize(2);
    }
}
