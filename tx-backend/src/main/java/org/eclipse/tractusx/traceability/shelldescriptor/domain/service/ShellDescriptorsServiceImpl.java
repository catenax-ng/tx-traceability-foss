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

package org.eclipse.tractusx.traceability.shelldescriptor.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.shelldescriptor.application.ShellDescriptorService;
import org.eclipse.tractusx.traceability.shelldescriptor.domain.model.ShellDescriptor;
import org.eclipse.tractusx.traceability.shelldescriptor.domain.repository.ShellDescriptorRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShellDescriptorsServiceImpl implements ShellDescriptorService {

    private final ShellDescriptorRepository shellDescriptorRepository;

    @Transactional
    public List<ShellDescriptor> determineExistingShellDescriptorsAndUpdate(List<ShellDescriptor> ownShellDescriptors) {
        log.info("Starting update of {} shell ownShellDescriptors.", ownShellDescriptors.size());
        Map<String, ShellDescriptor> existingDescriptors = shellDescriptorRepository.findAll().stream()
                .collect(Collectors.toMap(ShellDescriptor::getGlobalAssetId, Function.identity()));
        List<ShellDescriptor> descriptorsToSync = new ArrayList<>();
        ZonedDateTime now = ZonedDateTime.now();

        for (ShellDescriptor descriptor : ownShellDescriptors) {
            if (existingDescriptors.containsKey(descriptor.getGlobalAssetId())) {
                shellDescriptorRepository.update(existingDescriptors.get(descriptor.getGlobalAssetId()));
                log.info("Updated existing shellDescriptor with id {}.", descriptor.getGlobalAssetId());
            } else {
                descriptorsToSync.add((descriptor));
            }
        }
        log.info("Updated new shellDescriptors list size {}.", descriptorsToSync.size());
        shellDescriptorRepository.saveAll(descriptorsToSync);
        shellDescriptorRepository.removeDescriptorsByUpdatedBefore(now);

        log.info("Finished update of {} shell descriptors.", ownShellDescriptors.size());

        return descriptorsToSync;
    }

    @Transactional
    public void deleteAll(){
        shellDescriptorRepository.deleteAll();
    }

    @Transactional
    public List<ShellDescriptor> findAll(){
        return shellDescriptorRepository.findAll();
    }
}
