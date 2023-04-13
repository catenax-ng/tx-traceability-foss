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

import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PersistentBpnEdcRepository implements BpnEdcRepository {

    private final JpaBpnEdcRepository jpaBpnEdcRepository;

    public PersistentBpnEdcRepository(JpaBpnEdcRepository jpaBpnEdcRepository) {
        this.jpaBpnEdcRepository = jpaBpnEdcRepository;
    }

    @Override
    public PageResult<BpnEdc> findAll(Pageable pageable) {
        return new PageResult<>(jpaBpnEdcRepository.findAll(pageable), this::toBpnEdc);
    }

    @Override
    public Optional<BpnEdcEntity> findById(BpnEdcId id) {
        Optional<BpnEdcEntity> rv = jpaBpnEdcRepository.findById(id);
        return rv;
    }

    @Override
    public PageResult<BpnEdc> getBpnEdcMappings(Pageable pageable) {
        return new PageResult<>(jpaBpnEdcRepository.findAll(pageable), this::toBpnEdc);
    }

    @Override
    public void deleteById(BpnEdcId id) {
        jpaBpnEdcRepository.deleteById(id);
    }

    @Override
    public BpnEdc save(BpnEdcEntity entity) {
        jpaBpnEdcRepository.save(entity);
        return new BpnEdc(entity.getBpn(), entity.getUrl());
    }

    private BpnEdcEntity toEntity(BpnEdc bpnEdc) {
        return new BpnEdcEntity(
            bpnEdc.getBpn(),
            bpnEdc.getUrl()
        );
    }

    private BpnEdc toBpnEdc(BpnEdcEntity entity) {
        return new BpnEdc(
            entity.getBpn(),
            entity.getUrl()
        );
    }

}
