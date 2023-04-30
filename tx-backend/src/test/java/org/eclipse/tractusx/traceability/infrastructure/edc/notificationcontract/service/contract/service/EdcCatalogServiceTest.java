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
package org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.contract.service;

import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.asset.Asset;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.catalog.Catalog;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.offer.ContractOffer;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.policy.Policy;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.contract.model.CatalogRequestDTO;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.contract.model.CatalogRequestException;
import org.eclipse.tractusx.traceability.infrastructure.edc.properties.EdcProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EdcCatalogServiceTest {
    @Mock
    private RestTemplate restTemplateMock;

    @Mock
    private EdcProperties edcPropertiesMock;

    @InjectMocks
    private EdcCatalogService edcCatalogService;

    private final String providerConnectorUrl = "http://provider-connector-url.com";

    @Test
    void getCatalog_withValidProviderConnectorUrl_shouldReturnCatalog() throws URISyntaxException {
        // given
        CatalogRequestDTO catalogRequestDTO = new CatalogRequestDTO(providerConnectorUrl);
        Catalog catalogMock = testDataCatalog();
        ResponseEntity<Catalog> responseEntityMock = new ResponseEntity<>(catalogMock, HttpStatus.OK);
        final String catalogUrl = "http://edc-catalog-url.com";
        when(edcPropertiesMock.getCatalogPath()).thenReturn(catalogUrl);
        when(restTemplateMock.postForEntity(catalogUrl, catalogRequestDTO, Catalog.class))
                .thenReturn(responseEntityMock);

        // when
        Catalog catalog = edcCatalogService.getCatalog(providerConnectorUrl);

        // then
        assertEquals(catalogMock, catalog);
        verify(restTemplateMock).postForEntity(catalogUrl, catalogRequestDTO, Catalog.class);
    }

    @Test
    void getCatalog_withRestClientException_shouldThrowCatalogRequestException() throws URISyntaxException {
        // given
        CatalogRequestDTO catalogRequestDTO = new CatalogRequestDTO(providerConnectorUrl);
        final String catalogUrl = "http://edc-catalog-url.com";
        when(edcPropertiesMock.getCatalogPath()).thenReturn(catalogUrl);
        when(restTemplateMock.postForEntity(catalogUrl, catalogRequestDTO, Catalog.class))
                .thenThrow(new RestClientException("Something went wrong!"));

        // when, then
        assertThrows(CatalogRequestException.class, () -> {
            edcCatalogService.getCatalog(providerConnectorUrl);
        });

        verify(restTemplateMock).postForEntity(catalogUrl, catalogRequestDTO, Catalog.class);
    }

    private Catalog testDataCatalog() {
        return Catalog.Builder.newInstance()
                .id("catalog-id-123")
                .contractOffers(List.of(
                        ContractOffer.Builder.newInstance()
                                .id("contract-offer-id-1")
                                .asset(Asset.Builder.newInstance()
                                        .id("asset-id-1")
                                        .property("property-key-1", "property-value-1")
                                        .build())
                                .policy(Policy.Builder.newInstance()
                                        .build())
                                .build(),
                        ContractOffer.Builder.newInstance()
                                .id("contract-offer-id-2")
                                .asset(Asset.Builder.newInstance()
                                        .id("asset-id-2")
                                        .property("property-key-3", "property-value-3")
                                        .build())
                                .policy(Policy.Builder.newInstance()
                                        .build())
                                .build()))
                .build();
    }
}
