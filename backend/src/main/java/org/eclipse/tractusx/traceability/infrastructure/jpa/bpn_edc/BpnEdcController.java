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

package org.eclipse.tractusx.traceability.infrastructure.jpa.bpn_edc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Tag(name = "BpnEdc")
@RequestMapping(path = "/bpnedc", produces = "application/json", consumes = "application/json")
public class BpnEdcController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BpnEdcController.class);

    private final BpnEdcService service;

    public BpnEdcController(BpnEdcService service) {
        this.service = service;
    }

    @Operation(operationId = "getBpnEdcs",
            summary = "Get BPN EDC URL mappings by pagination",
            tags = {"BpnEdc"},
            description = "The endpoint returns a paged result of BPN EDC URL mappings.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns the paged result found"),
            @ApiResponse(responseCode = "401", description = "Authorization failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")})
    @GetMapping("")
    public PageResult<BpnEdc> getBpnEdcs(Pageable pageable) {
        return service.getBpnEdcMappings(pageable);
    }

    @Operation(operationId = "createBpnEdcUrlMappings",
            summary = "Creates BPN EDC URL mappings",
            tags = {"BpnEdc"},
            description = "The endpoint creates BPN EDC URL mappings",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Created."),
            @ApiResponse(responseCode = "401", description = "Authorization failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")})
    @PostMapping("")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createBpnEdcUrlMappings(@Valid @RequestBody BpnEdcRequest request) {
        LOGGER.info("----------POST------------");
        service.createBpnEdcUrlMappings(request.bpn(), request.urls());
    }

    @Operation(operationId = "deleteBpnEdcUrlMappings",
            summary = "Deletes BPN EDC URL mappings",
            tags = {"BpnEdc"},
            description = "The endpoint deletes BPN EDC URL mappings",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Deleted."),
            @ApiResponse(responseCode = "401", description = "Authorization failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")})
    @DeleteMapping("")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBpnEdcUrlMappings(@Valid @RequestBody BpnEdcRequest request) {
        LOGGER.info("----------DELETE------------");
        service.deleteBpnEdcUrlMappings(request.bpn(), request.urls());
    }

}
