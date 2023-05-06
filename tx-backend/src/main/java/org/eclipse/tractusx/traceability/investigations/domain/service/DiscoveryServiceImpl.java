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

package org.eclipse.tractusx.traceability.investigations.domain.service;

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.traceability.bpn.mapping.domain.ports.BpnEdcMappingRepository;
import org.eclipse.tractusx.traceability.infrastructure.edc.properties.EdcProperties;
import org.eclipse.tractusx.traceability.investigations.infrastructure.model.feign.ConnectorDiscoveryMappingResponse;
import org.eclipse.tractusx.traceability.investigations.infrastructure.repository.feign.FeignDiscoveryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

@Component
@RequiredArgsConstructor
public class DiscoveryServiceImpl implements DiscoveryService {

    private static final Logger logger = LoggerFactory.getLogger(DiscoveryServiceImpl.class);

    private final FeignDiscoveryRepository feignDiscoveryRepository;

    private final BpnEdcMappingRepository bpnEdcMappingRepository;

    private final EdcProperties edcProperties;

    @Override
    public List<String> getEdcUrlsByBPN(String bpn) {
        List<ConnectorDiscoveryMappingResponse> response;
        try {
            response = feignDiscoveryRepository.getConnectorEndpointMappings(List.of(bpn));
            if (response == null) {
                response = Collections.emptyList();
            }
        } catch (Exception e) {
            logger.warn("Exception during retrieving EDC Urls from DiscoveryService for {} bpn. Http Message: {} " +
                    "This is okay if the discovery service is not reachable from the specific environment", bpn, e.getMessage());
            response = Collections.emptyList();
        }

        Map<String, List<String>> bpnToEndpointMappings = emptyIfNull(response).stream()
                .collect(Collectors.toMap(ConnectorDiscoveryMappingResponse::bpn, ConnectorDiscoveryMappingResponse::connectorEndpoint));

        List<String> endpoints = bpnToEndpointMappings.get(bpn);

        if (endpoints == null) {
            logger.warn("No connector endpoint registered for {} bpn", bpn);
            endpoints = Collections.emptyList();
        }
        return combineDiscoveredUrlsAndFallbackUrls(endpoints, getEdcUrlsByDatabase(bpn));
    }

    @Override
    public String getApplicationSenderUrl() {
        return edcProperties.getProviderEdcUrl();
    }

    private List<String> getEdcUrlsByDatabase(String bpn) {
        if (bpnEdcMappingRepository.exists(bpn)) {
            return List.of(bpnEdcMappingRepository.findById(bpn).url());
        }
        return Collections.emptyList();
    }

    private List<String> combineDiscoveredUrlsAndFallbackUrls(List<String> discoveredUrls, List<String> fallbackUrls) {
        Set<String> combinedUrlSet = new HashSet<>();
        combinedUrlSet.addAll(discoveredUrls);
        combinedUrlSet.addAll(fallbackUrls);
        return new ArrayList<>(combinedUrlSet);
    }

}
