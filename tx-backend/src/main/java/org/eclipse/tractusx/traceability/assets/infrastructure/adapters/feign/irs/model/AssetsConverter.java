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

package org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.model.QualityType;
import org.eclipse.tractusx.traceability.assets.domain.model.ShellDescriptor;
import org.eclipse.tractusx.traceability.assets.domain.ports.BpnRepository;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AssetsConverter {

    public static final String EMPTY_TEXT = "--";

    private final BpnRepository bpnRepository;

    private final TraceabilityProperties traceabilityProperties;

    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final String SINGLE_LEVEL_USAGE_AS_BUILT = "SingleLevelUsageAsBuilt";
    private static final String ASSEMBLY_PART_RELATIONSHIP = "AssemblyPartRelationship";

    public AssetsConverter(BpnRepository bpnRepository, TraceabilityProperties traceabilityProperties) {
        this.bpnRepository = bpnRepository;
        this.traceabilityProperties = traceabilityProperties;
    }

    public List<Asset> readAndConvertAssets() {
        try {
            InputStream file = AssetsConverter.class.getResourceAsStream("/data/irs_assets_v2.json");
            JobResponse response = mapper.readValue(file, JobResponse.class);

            return convertAssets(response);
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    public List<Asset> convertAssets(List<ShellDescriptor> items) {
        return items.stream()
                .map(this::toAsset)
                .toList();
    }

    public List<Asset> convertAssets(JobResponse response) {
        List<SerialPartTypization> allParts = response.serialPartTypizations();

        Map<String, String> shortIds = response.shells().stream()
                .collect(Collectors.toMap(Shell::identification, Shell::idShort));

        Map<String, List<Relationship>> supplierPartsMap = response.relationships().stream()
                .filter(relationship -> ASSEMBLY_PART_RELATIONSHIP.equals(relationship.aspectType().getAspectName()))
                .collect(Collectors.groupingBy(Relationship::childCatenaXId, Collectors.toList()));


        Map<String, List<Relationship>> customerPartsMap = response.relationships().stream()
                .filter(relationship -> SINGLE_LEVEL_USAGE_AS_BUILT.equals(relationship.aspectType().getAspectName()))
                .collect(Collectors.groupingBy(Relationship::childCatenaXId, Collectors.toList()));

        return allParts.stream()
                .map(part -> new Asset(
                        part.catenaXId(),
                        defaultValue(shortIds.get(part.catenaXId())),
                        defaultValue(part.partTypeInformation().nameAtManufacturer()),
                        defaultValue(part.partTypeInformation().manufacturerPartId()),
                        partInstanceId(part),
                        manufacturerId(part),
                        batchId(part),
                        manufacturerName(part),
                        defaultValue(part.partTypeInformation().nameAtCustomer()),
                        defaultValue(part.partTypeInformation().customerPartId()),
                        manufacturingDate(part),
                        manufacturingCountry(part),
                        getPartOwner(supplierPartsMap, customerPartsMap, part.catenaXId(), part.getLocalId(LocalIdType.MANUFACTURER_ID)),
                        getPartsFromRelationships(supplierPartsMap, shortIds, part.catenaXId()),
                        getPartsFromRelationships(customerPartsMap, shortIds, part.catenaXId()),
                        false,
                        QualityType.OK,
                        van(part)
                )).toList();
    }

    private Asset toAsset(ShellDescriptor shellDescriptor) {
        String manufacturerId = shellDescriptor.manufacturerId();
        String manufacturerName = bpnRepository.findManufacturerName(manufacturerId).orElse(EMPTY_TEXT);
        return new Asset(
                shellDescriptor.globalAssetId(),
                shellDescriptor.idShort(),
                shellDescriptor.idShort(),
                defaultValue(shellDescriptor.manufacturerPartId()),
                defaultValue(shellDescriptor.partInstanceId()),
                defaultValue(manufacturerId),
                defaultValue(shellDescriptor.batchId()),
                manufacturerName,
                shellDescriptor.idShort(),
                shellDescriptor.manufacturerPartId(),
                null,
                EMPTY_TEXT,
                Owner.OWN,
                Collections.emptyList(),
                Collections.emptyList(),
                false,
                QualityType.OK,
                EMPTY_TEXT
        );
    }

    private Owner getPartOwner(Map<String, List<Relationship>> supplierParts, Map<String, List<Relationship>> customerParts, String catenaXId, Optional<String> manufacturerId) {

        if (manufacturerId.isPresent() && traceabilityProperties.getBpn().value().equals(manufacturerId.get())) {
            return Owner.OWN;
        }

        if (supplierParts.containsKey(catenaXId)) {
            return Owner.SUPPLIER;
        }
        if (customerParts.containsKey(catenaXId)) {
            return Owner.CUSTOMER;
        }
        return Owner.OWN;
    }


    private String manufacturerName(SerialPartTypization part) {
        String manufacturerId = manufacturerId(part);

        return bpnRepository.findManufacturerName(manufacturerId).orElse(EMPTY_TEXT);
    }

    private String manufacturerId(SerialPartTypization part) {
        return part.getLocalId(LocalIdType.MANUFACTURER_ID)
                .orElse(EMPTY_TEXT);
    }

    private String batchId(SerialPartTypization part) {
        return part.getLocalId(LocalIdType.BATCH_ID)
                .orElse(EMPTY_TEXT);
    }

    private String partInstanceId(SerialPartTypization part) {
        return part.getLocalId(LocalIdType.PART_INSTANCE_ID)
                .orElse(EMPTY_TEXT);
    }

    private String manufacturingCountry(SerialPartTypization part) {
        if (part.manufacturingInformation() == null) {
            return EMPTY_TEXT;
        }
        return part.manufacturingInformation().country();
    }

    private Instant manufacturingDate(SerialPartTypization part) {
        if (part.manufacturingInformation() == null) {
            return null;
        }

        return Optional.ofNullable(part.manufacturingInformation().date())
                .map(Date::toInstant)
                .orElse(null);
    }

    private String defaultValue(String value) {
        if (!StringUtils.hasText(value)) {
            return EMPTY_TEXT;
        }
        return value;
    }

    private List<Asset.Descriptions> getPartsFromRelationships(Map<String, List<Relationship>> relationships, Map<String, String> shortIds, String catenaXId) {
        return Optional.ofNullable(relationships.get(catenaXId))
                .orElse(Collections.emptyList())
                .stream()
                .map(child -> new Asset.Descriptions(child.catenaXId(), shortIds.get(child.catenaXId())))
                .toList();
    }

    private String van(SerialPartTypization part) {
        return part.getLocalId(LocalIdType.VAN)
                .orElse(EMPTY_TEXT);
    }

}
