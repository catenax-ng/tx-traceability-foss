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

import java.util.List;

@Component
public class BpnEdcService {


    private final BpnEdcRepository bpnEdcRepository;

    public BpnEdcService(BpnEdcRepository bpnEdcRepository) {
        this.bpnEdcRepository = bpnEdcRepository;
    }

    public PageResult<BpnEdc> getBpnEdcMappings(Pageable pageable) {
        return bpnEdcRepository.getBpnEdcMappings(pageable);
    }

    public void createBpnEdcUrlMapping(String bpn, String url) {
        if(bpnEdcRepository.findById(bpn).isEmpty()) {
            bpnEdcRepository.save(new BpnEdcEntity(bpn, url));
        }
    }

    public void deleteBpnEdcUrlMapping(String bpn) {
        bpnEdcRepository.deleteById(bpn);
    }

}
