/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.assets.infrastructure.importJob.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.ImportJob;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.ImportJobStatus;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.model.AssetAsPlannedEntity;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@SuperBuilder
@Table(name = "import_job")
public class ImportJobEntity {
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "asset_as_built_id")
    @ToString.Exclude
    public AssetAsBuiltEntity assetAsBuilt;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private Instant startedOn;
    private Instant completedOn;
    @Enumerated(EnumType.STRING)
    private ImportJobStatus importJobStatus;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "asset_as_planned_id")
    @ToString.Exclude
    private AssetAsPlannedEntity assetAsPlanned;

    public static ImportJobEntity from(ImportJob importJob) {
        return ImportJobEntity.builder()
                .id(importJob.getId().toString())
                .startedOn(importJob.getStartedOn())
                .completedOn(importJob.getCompletedOn())
                .importJobStatus(importJob.getStatus())
                .build();

    }

    public ImportJob toDomain() {
        return ImportJob.builder()
                .id(UUID.fromString(id))
                .startedOn(startedOn)
                .completedOn(completedOn)
                .status(importJobStatus)
                .build();
    }
}
