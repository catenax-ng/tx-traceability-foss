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

package org.eclipse.tractusx.traceability.discovery.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.bpn.mapping.domain.ports.BpnEdcMappingRepository;
import org.eclipse.tractusx.traceability.discovery.domain.model.Discovery;
import org.eclipse.tractusx.traceability.discovery.domain.repository.DiscoveryRepository;
import org.eclipse.tractusx.traceability.infrastructure.edc.properties.EdcProperties;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.eclipse.tractusx.traceability.discovery.domain.model.Discovery.mergeDiscoveries;
import static org.eclipse.tractusx.traceability.discovery.domain.model.Discovery.toDiscovery;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscoveryServiceImpl implements DiscoveryService {

    private final DiscoveryRepository discoveryRepository;

    private final BpnEdcMappingRepository bpnEdcMappingRepository;

    private final EdcProperties edcProperties;


    @Override
    public Discovery getDiscoveryByBPN(String bpn) {
        List<Discovery> discoveryList = new ArrayList<>();
        Optional<Discovery> optionalDiscoveryFromDiscoveryService = getOptionalDiscoveryByBpnFromDiscoveryService(bpn);
        optionalDiscoveryFromDiscoveryService.ifPresent(discovery -> log.info("Retrieved discovery by bpn from edcDiscoveryService receiverUrls: {}, senderUrls: {}", discovery.getReceiverUrls().toString(), discovery.getSenderUrl()));
        optionalDiscoveryFromDiscoveryService.ifPresent(discoveryList::add);
        Optional<Discovery> optionalDiscoveryFromBpnDatabase = getOptionalDiscoveryFromBpnDatabase(bpn);
        optionalDiscoveryFromBpnDatabase.ifPresent(discovery -> log.info("Retrieved discovery by bpn from BPN Mapping Table receiverUrls: {}, senderUrls: {}", discovery.getReceiverUrls().toString(), discovery.getSenderUrl()));
        optionalDiscoveryFromBpnDatabase.ifPresent(discoveryList::add);
        return mergeDiscoveries(discoveryList);
    }

    @NotNull
    private Optional<Discovery> getOptionalDiscoveryByBpnFromDiscoveryService(String bpn) {
        return discoveryRepository.retrieveDiscoveryByFinderAndEdcDiscoveryService(bpn);
    }

    @NotNull
    private Optional<Discovery> getOptionalDiscoveryFromBpnDatabase(String bpn) {
        if (bpnEdcMappingRepository.exists(bpn)) {
            String receiverUrl = bpnEdcMappingRepository.findByIdOrThrowNotFoundException(bpn).url();
            Discovery discovery = toDiscovery(receiverUrl, edcProperties.getProviderEdcUrl());
            return Optional.of(discovery);
        }
        return Optional.empty();
    }


}
