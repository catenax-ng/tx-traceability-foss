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

package org.eclipse.tractusx.traceability.assets.infrastructure.repository.jpa;

import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.model.QualityType;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository.AssetAsBuiltRepositoryImpl;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository.JpaAssetAsBuiltRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.eclipse.tractusx.traceability.testdata.AssetTestDataFactory.createAssetTestData;

@ExtendWith(MockitoExtension.class)
class PersistentAssetsAsBuiltRepositoryTest {

    @InjectMocks
    private AssetAsBuiltRepositoryImpl persistentAssetsRepository;

    @Mock
    private JpaAssetAsBuiltRepository jpaAssetsRepository;

    @Test
    void testToAsset() {
        // Given
        AssetAsBuiltEntity entity = AssetAsBuiltEntity.builder()
                .id("1")
                .idShort("1234")
                .nameAtManufacturer("Manufacturer Name")
                .manufacturerPartId("customer123")
                .semanticModelId("PI001")
                .manufacturerId("manuId")
                .manufacturerName("manuName")
                .nameAtCustomer("Customer Name")
                .customerPartId("customerPartId")
                .manufacturingDate(Instant.ofEpochSecond(11111111L))
                .manufacturingCountry("manu456")
                .owner(Owner.OWN)
                .qualityType(QualityType.CRITICAL)
                .van("van123")
                .childDescriptors(List.of(AssetAsBuiltEntity.ChildDescription.builder()
                                .id("child1")
                                .idShort("desc1")
                                .build(),
                        AssetAsBuiltEntity.ChildDescription.builder()
                                .id("child2")
                                .idShort("desc2")
                                .build())

                )
                .parentDescriptors(List.of(AssetAsBuiltEntity.ParentDescription.builder()
                                        .id("parent1")
                                        .idShort("desc1")
                                        .build(),
                                AssetAsBuiltEntity.ParentDescription.builder()
                                        .id("parent2")
                                        .idShort("desc2")
                                        .build()

                        )

                )
                .build();


        // when
        Asset asset = AssetAsBuiltEntity.toDomain(entity);


        // then
        Asset expected = createAssetTestData();

        Assertions.assertEquals(asset.getId(), expected.getId());
        Assertions.assertEquals(asset.getIdShort(), expected.getIdShort());
        Assertions.assertEquals(asset.getSemanticModel().getNameAtManufacturer(), expected.getSemanticModel().getNameAtManufacturer());
        Assertions.assertEquals(asset.getSemanticModel().getManufacturerPartId(), expected.getSemanticModel().getManufacturerPartId());
        Assertions.assertEquals(asset.getManufacturerId(), expected.getManufacturerId());
        Assertions.assertEquals(asset.getManufacturerName(), expected.getManufacturerName());
        Assertions.assertEquals(asset.getSemanticModel().getNameAtCustomer(), expected.getSemanticModel().getNameAtCustomer());
        Assertions.assertEquals(asset.getSemanticModel().getCustomerPartId(), expected.getSemanticModel().getCustomerPartId());
        Assertions.assertEquals(asset.getSemanticModel().getManufacturingCountry(), expected.getSemanticModel().getManufacturingCountry());
        Assertions.assertEquals(asset.getOwner(), expected.getOwner());
        Assertions.assertEquals(asset.getChildRelations(), expected.getChildRelations());
        Assertions.assertEquals(asset.getParentRelations(), expected.getParentRelations());
        Assertions.assertEquals(asset.isUnderInvestigation(), expected.isUnderInvestigation());
        Assertions.assertEquals(asset.getQualityType(), expected.getQualityType());
        Assertions.assertEquals(asset.getVan(), expected.getVan());
    }

}
