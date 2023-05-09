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

package org.eclipse.tractusx.traceability.assets;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Slf4j
@AllArgsConstructor
@ArraySchema(arraySchema = @Schema(description = "Assets"), maxItems = Integer.MAX_VALUE)
public final class Asset {
	@ApiModelProperty(example = "urn:uuid:ceb6b964-5779-49c1-b5e9-0ee70528fcbd")
	private final String id;
	@ApiModelProperty(example = "--")
	private final String idShort;
	@ApiModelProperty(example = "Door f-r")
	private final String nameAtManufacturer;
	@ApiModelProperty(example = "33740332-54")
	private final String manufacturerPartId;
	@ApiModelProperty(example = "NO-297452866581906730261974")
	private final String partInstanceId;
	@ApiModelProperty(example = "BPNL00000003CSGV")
	private final String manufacturerId;
	@ApiModelProperty(example = "--")
	private final String batchId;
	@ApiModelProperty(example = "Tier C")
	private String manufacturerName;
	@ApiModelProperty(example = "Door front-right")
	private final String nameAtCustomer;
	@ApiModelProperty(example = "33740332-54")
	private final String customerPartId;
	@ApiModelProperty(example = "2022-02-04T13:48:54Z")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private final Instant manufacturingDate;
	@ApiModelProperty(example = "DEU")
	private final String manufacturingCountry;
	@ApiModelProperty(example = "CUSTOMER")
	private final Owner owner;

    @ArraySchema(arraySchema = @Schema(description = "Child relationships"), maxItems = Integer.MAX_VALUE)
	private List<Descriptions> childDescriptions;
    @ArraySchema(arraySchema = @Schema(description = "Parent relationships"), maxItems = Integer.MAX_VALUE)
    private List<Descriptions> parentDescriptions;
	@ApiModelProperty(example = "false")
	private boolean underInvestigation;
	@ApiModelProperty(example = "Ok")
	private QualityType qualityType;
	@ApiModelProperty(example = "--")
	private String van;

	public String getBatchId() {
		return batchId;
	}

	public void updateQualityType(QualityType qualityType) {
		this.qualityType = qualityType;
	}

	public String getId() {
		return id;
	}

	public String getIdShort() {
		return idShort;
	}

	public String getNameAtManufacturer() {
		return nameAtManufacturer;
	}

	public String getManufacturerPartId() {
		return manufacturerPartId;
	}

	public String getManufacturerId() {
		return manufacturerId;
	}

	public String getManufacturerName() {
		return manufacturerName;
	}

	public String getNameAtCustomer() {
		return nameAtCustomer;
	}

	public String getCustomerPartId() {
		return customerPartId;
	}

	public Instant getManufacturingDate() {
		return manufacturingDate;
	}

	public String getManufacturingCountry() {
		return manufacturingCountry;
	}

    public Owner getOwner() {
        return owner;
    }

    public String getPartInstanceId() {
		return partInstanceId;
	}

	public List<Descriptions> getChildDescriptions() {
		return childDescriptions;
	}

    public List<Descriptions> getParentDescriptions() {
        return parentDescriptions;
    }

    public void setParentDescriptions(List<Descriptions> descriptions) {
        this.parentDescriptions = Collections.unmodifiableList(descriptions);
        log.info("Asset: setParentDescriptions {}", this.parentDescriptions);
    }

	public QualityType getQualityType() {
		return qualityType;
	}

	public boolean isUnderInvestigation() {
		return underInvestigation;
	}

    public void setUnderInvestigation(boolean underInvestigation) {
        this.underInvestigation = underInvestigation;
    }

    public record Descriptions(
		@ApiModelProperty(example = "urn:uuid:a4a26b9c-9460-4cc5-8645-85916b86adb0") String id,
		@ApiModelProperty(example = "null") String idShort) {
	}

	public String getVan() {
		return van;
	}

    @Override
    public String toString() {
        return "Asset{" +
                "id='" + id + '\'' +
                ", idShort='" + idShort + '\'' +
                ", nameAtManufacturer='" + nameAtManufacturer + '\'' +
                ", manufacturerPartId='" + manufacturerPartId + '\'' +
                ", partInstanceId='" + partInstanceId + '\'' +
                ", manufacturerId='" + manufacturerId + '\'' +
                ", batchId='" + batchId + '\'' +
                ", manufacturerName='" + manufacturerName + '\'' +
                ", nameAtCustomer='" + nameAtCustomer + '\'' +
                ", customerPartId='" + customerPartId + '\'' +
                ", manufacturingDate=" + manufacturingDate +
                ", manufacturingCountry='" + manufacturingCountry + '\'' +
                ", owner=" + owner +
                ", childDescriptions=" + childDescriptions +
                ", parentDescriptions=" + parentDescriptions +
                ", underInvestigation=" + underInvestigation +
                ", qualityType=" + qualityType +
                ", van='" + van + '\'' +
                '}';
    }
}
