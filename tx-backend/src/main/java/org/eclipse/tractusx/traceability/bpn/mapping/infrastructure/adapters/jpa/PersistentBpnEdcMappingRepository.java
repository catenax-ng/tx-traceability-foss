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

package org.eclipse.tractusx.traceability.bpn.mapping.infrastructure.adapters.jpa;

import org.eclipse.tractusx.traceability.bpn.mapping.domain.model.BpnEdcMapping;
import org.eclipse.tractusx.traceability.bpn.mapping.domain.model.BpnEdcMappingNotFoundException;
import org.eclipse.tractusx.traceability.bpn.mapping.domain.ports.BpnEdcMappingRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersistentBpnEdcMappingRepository implements BpnEdcMappingRepository {

    private final JpaBpnEdcRepository jpaBpnEdcRepository;

    public PersistentBpnEdcMappingRepository(JpaBpnEdcRepository jpaBpnEdcRepository) {
        this.jpaBpnEdcRepository = jpaBpnEdcRepository;
    }

    @Override
    public BpnEdcMapping findById(String bpn) {
        return jpaBpnEdcRepository.findById(bpn)
                .map(this::toBpnEdc)
                .orElseThrow(() -> new BpnEdcMappingNotFoundException("EDC URL mapping with BPN %s was not found."
                        .formatted(bpn)));
    }
    @Override
    public boolean exists(String bpn) {
        return jpaBpnEdcRepository.findById(bpn).isPresent();
    }

    @Override
    public List<BpnEdcMapping> findAll() {
        return jpaBpnEdcRepository.findAll().stream().map(this::toBpnEdc).toList();
    }

    @Override
    public void deleteById(String bpn) {
        jpaBpnEdcRepository.deleteById(bpn);
    }

    @Override
    public void save(BpnEdcMappingEntity entity) {
        jpaBpnEdcRepository.save(entity);
    }

    private BpnEdcMapping toBpnEdc(BpnEdcMappingEntity entity) {
        return new BpnEdcMapping(
                entity.getBpn(),
                entity.getUrl()
        );
    }

}
