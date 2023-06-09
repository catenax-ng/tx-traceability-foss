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
package org.eclipse.tractusx.traceability.assets.domain.model;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.tractusx.traceability.assets.infrastructure.model.AssetBaseEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.repository.rest.irs.model.response.semanticdatamodel.ManufacturingInformation;
import org.eclipse.tractusx.traceability.assets.infrastructure.repository.rest.irs.model.response.semanticdatamodel.PartTypeInformation;
import org.eclipse.tractusx.traceability.shelldescriptor.domain.model.ShellDescriptor;

import java.time.Instant;

@Builder
@Data
public class SemanticModel {
    private Instant manufacturingDate;
    private String manufacturingCountry;
    private String manufacturerPartId;
    private String customerPartId;
    private String nameAtManufacturer;
    private String nameAtCustomer;

    public static SemanticModel from(AssetBaseEntity assetBaseEntity) {
        return SemanticModel.builder()
                .customerPartId(assetBaseEntity.getCustomerPartId())
                .manufacturerPartId(assetBaseEntity.getManufacturerPartId())
                .manufacturingCountry(assetBaseEntity.getManufacturingCountry())
                .manufacturingDate(assetBaseEntity.getManufacturingDate())
                .nameAtCustomer(assetBaseEntity.getNameAtCustomer())
                .nameAtManufacturer(assetBaseEntity.getNameAtManufacturer())
                .build();
    }

    public static SemanticModel from(ShellDescriptor shellDescriptor) {
        return SemanticModel.builder()
                .manufacturerPartId(shellDescriptor.getManufacturerPartId())
                .nameAtManufacturer(shellDescriptor.getIdShort())
                .nameAtCustomer(shellDescriptor.getIdShort())
                .manufacturingCountry("--")
                .manufacturerPartId(defaultValue(shellDescriptor.getManufacturerPartId()))
                .build();
    }

    public static SemanticModel from(PartTypeInformation partTypeInformation, ManufacturingInformation manufacturingInformation) {
        return SemanticModel.builder()
                .manufacturerPartId(defaultValue(partTypeInformation.manufacturerPartId()))
                .nameAtManufacturer(defaultValue(partTypeInformation.nameAtManufacturer()))
                .customerPartId(defaultValue(partTypeInformation.customerPartId()))
                .nameAtCustomer(defaultValue(partTypeInformation.nameAtCustomer()))
                .manufacturingCountry(defaultValue(manufacturingInformation.country()))
                .manufacturingDate(manufacturingInformation.date().toInstant())
                .manufacturerPartId(defaultValue(partTypeInformation.manufacturerPartId()))
                .build();
    }

    private static String defaultValue(String value) {
        final String EMPTY_TEXT = "--";
        if (StringUtils.isBlank(value)) {
            return EMPTY_TEXT;
        }
        return value;
    }

}
