/********************************************************************************
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

package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;
import java.util.Objects;

public enum Aspect {
    BATCH("urn:samm:io.catenax.batch:2.0.0#Batch"),
    SERIAL_PART("urn:bamm:io.catenax.serial_part:1.0.1#SerialPart"),
    // TODO: update to urn:bamm:io.catenax.single_level_bom_as_built:2.0.0#SingleLevelBomAsBuilt, when its available in the semantic hub
    SINGLE_LEVEL_BOM_AS_BUILT("urn:bamm:io.catenax.single_level_bom_as_built:1.0.0#SingleLevelBomAsBuilt"), // We are currently not able to use the 2.0.0 version, because this version is not available in the semantic hub https://github.com/eclipse-tractusx/sldt-semantic-models/tree/main/io.catenax.single_level_bom_as_built and IRS is not able to map the 2.0.0 version. We have to use the full urn because of https://github.com/eclipse-tractusx/traceability-foss/issues/823 (since irs 4.8.0)
    SINGLE_LEVEL_USAGE_AS_BUILT("urn:bamm:io.catenax.single_level_usage_as_built:2.0.0#SingleLevelUsageAsBuilt"),
    SINGLE_LEVEL_BOM_AS_PLANNED("urn:bamm:io.catenax.single_level_bom_as_planned:2.0.0#SingleLevelBomAsPlanned"),
    PART_SITE_INFORMATION_AS_PLANNED("urn:bamm:io.catenax.part_site_information_as_planned:1.0.0#PartSiteInformationAsPlanned"),
    PART_AS_PLANNED("urn:bamm:io.catenax.part_as_planned:1.0.1#PartAsPlanned"),
    JUST_IN_SEQUENCE_PART("urn:bamm:io.catenax.just_in_sequence_part:1.0.0#JustInSequencePart"),
    TRACTION_BATTERY_CODE("urn:bamm:io.catenax.traction_battery_code:1.0.0#TractionBatteryCode");

    private final String aspectName;

    Aspect(String aspectName) {
        this.aspectName = aspectName;
    }

    @JsonValue
    public String getAspectName() {
        return aspectName;
    }

    public static List<String> downwardAspectsForAssetsAsBuilt() {
        return List.of(BATCH.getAspectName(), SERIAL_PART.getAspectName(), SINGLE_LEVEL_BOM_AS_BUILT.getAspectName(),
                JUST_IN_SEQUENCE_PART.getAspectName(), TRACTION_BATTERY_CODE.getAspectName());
    }

    public static List<String> upwardAspectsForAssetsAsBuilt() {
        return List.of(BATCH.getAspectName(), SERIAL_PART.getAspectName(), SINGLE_LEVEL_USAGE_AS_BUILT.getAspectName(),
                JUST_IN_SEQUENCE_PART.getAspectName(), TRACTION_BATTERY_CODE.getAspectName());
    }

    public static List<String> downwardAspectsForAssetsAsPlanned() {
        return List.of(PART_AS_PLANNED.getAspectName(), PART_SITE_INFORMATION_AS_PLANNED.getAspectName());
    }


    public static boolean isMainAspect(String aspect) {
        assert Objects.nonNull(aspect);
        return aspect.contains(Aspect.PART_AS_PLANNED.getAspectName()) ||
                aspect.contains(Aspect.SERIAL_PART.getAspectName()) ||
                aspect.contains(Aspect.BATCH.getAspectName()) ||
                aspect.contains(Aspect.JUST_IN_SEQUENCE_PART.getAspectName());
    }

    public static boolean isAsBuiltMainAspect(String aspect) {
        return aspect.contains(Aspect.SERIAL_PART.getAspectName()) ||
                aspect.contains(Aspect.BATCH.getAspectName()) ||
                aspect.contains(Aspect.JUST_IN_SEQUENCE_PART.getAspectName());
    }

    public static boolean isAsPlannedMainAspect(String aspect) {
        return aspect.contains(Aspect.PART_AS_PLANNED.getAspectName());
    }

    public static boolean isPartSiteInformationAsPlanned(String aspect){
        return aspect.contains(Aspect.PART_SITE_INFORMATION_AS_PLANNED.getAspectName());
    }

    public static boolean isTractionBatteryCode(String aspect){
        return aspect.contains(Aspect.TRACTION_BATTERY_CODE.getAspectName());
    }

    public static boolean isUpwardRelationshipAsBuilt(String aspect){
        return aspect.contains(Aspect.SINGLE_LEVEL_BOM_AS_BUILT.getAspectName());
    }

    public static boolean isDownwardRelationshipAsBuilt(String aspect){
        return aspect.contains(Aspect.SINGLE_LEVEL_USAGE_AS_BUILT.getAspectName());
    }

    public static boolean isUpwardRelationshipAsPlanned(String aspect){
        return aspect.contains(Aspect.SINGLE_LEVEL_BOM_AS_PLANNED.getAspectName());
    }
}
