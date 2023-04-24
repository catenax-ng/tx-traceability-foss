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
package org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.catalog.service;

import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.BadRequestException;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.catalog.Catalog;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.catalog.model.CatalogRequestDTO;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.catalog.model.CatalogRequestException;
import org.eclipse.tractusx.traceability.infrastructure.edc.properties.EdcProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static java.lang.String.format;
import static org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.configuration.EdcRestTemplateConfiguration.EDC_REST_TEMPLATE;

@Component
public class EdcCatalogService {
    private final RestTemplate restTemplate;
    private final EdcProperties properties;

    private static final Logger logger = LoggerFactory.getLogger(EdcCatalogService.class);


    public EdcCatalogService(@Qualifier(EDC_REST_TEMPLATE) RestTemplate restTemplate, EdcProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    public Catalog getCatalog(String providerConnectorUrl) {

        CatalogRequestDTO catalogRequestDTO = new CatalogRequestDTO(providerConnectorUrl);
        final ResponseEntity<Catalog> responseEntity;

        try {
            logger.info("Requesting HTTP GET Catalog: {} and body {} ...", properties.getCatalogPath(), catalogRequestDTO);
            responseEntity = restTemplate.postForEntity(properties.getCatalogPath(), catalogRequestDTO, Catalog.class);
        } catch (RestClientException e) {
            throw new CatalogRequestException(e);
        }

        HttpStatusCode responseCode = responseEntity.getStatusCode();

        if (requestSuccessful(responseCode)) {
            return responseEntity.getBody();
        } else {
            throw new BadRequestException(format("Control plane responded with: %s %s", responseCode.value(), responseEntity.getBody()));
        }
    }

    private boolean requestSuccessful(HttpStatusCode responseCode) {
        return responseCode.value() > 199 && responseCode.value() < 300;
    }
}
