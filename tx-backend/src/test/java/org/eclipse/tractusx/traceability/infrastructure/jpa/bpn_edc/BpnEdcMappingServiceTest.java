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

package org.eclipse.tractusx.traceability.infrastructure.jpa.bpn_edc;

import org.eclipse.tractusx.traceability.bpnmapping.domain.model.BpnEdcMappingException;
import org.eclipse.tractusx.traceability.bpnmapping.domain.model.BpnEdcMappingNotFoundException;
import org.eclipse.tractusx.traceability.bpnmapping.domain.ports.BpnEdcMappingRepository;
import org.eclipse.tractusx.traceability.bpnmapping.domain.service.BpnEdcMappingService;
import org.eclipse.tractusx.traceability.bpnmapping.infrastructure.adapters.jpa.BpnEdcMappingEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;

import static org.mockito.Mockito.*;

class BpnEdcMappingServiceTest {

    private BpnEdcMappingService bpnEdcMappingService;

    @Mock
    private BpnEdcMappingRepository bpnEdcMappingRepositoryMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        bpnEdcMappingService = new BpnEdcMappingService(bpnEdcMappingRepositoryMock);
    }

    @Test
    @DisplayName("Test getBpnEdcMappings")
    void testGetBpnEdcMappings() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        bpnEdcMappingService.getBpnEdcMappings(pageRequest);
        verify(bpnEdcMappingRepositoryMock, times(1)).findAllPaged(pageRequest);
    }

    @Test
    @DisplayName("Test createBpnEdcMapping")
    void testCreateBpnEdcMapping() {
        String bpn = "12345";
        String url = "https://example.com/12345";
        when(bpnEdcMappingRepositoryMock.exists(bpn)).thenReturn(false);
        bpnEdcMappingService.createBpnEdcMapping(bpn, url);
        verify(bpnEdcMappingRepositoryMock, times(1)).save(any(BpnEdcMappingEntity.class));
    }

    @Test
    @DisplayName("Test createBpnEdcMapping with existing mapping")
    void testCreateBpnEdcMappingWithExistingMapping() {
        String bpn = "12345";
        String url = "https://example.com/12345";
        when(bpnEdcMappingRepositoryMock.exists(bpn)).thenReturn(true);
        Assertions.assertThrows(BpnEdcMappingException.class, () -> {
            bpnEdcMappingService.createBpnEdcMapping(bpn, url);
        });
        verify(bpnEdcMappingRepositoryMock, never()).save(any(BpnEdcMappingEntity.class));
    }

    @Test
    @DisplayName("Test deleteBpnEdcMapping")
    void testDeleteBpnEdcMapping() {
        String bpn = "12345";
        when(bpnEdcMappingRepositoryMock.exists(bpn)).thenReturn(true);
        bpnEdcMappingService.deleteBpnEdcMapping(bpn);
        verify(bpnEdcMappingRepositoryMock, times(1)).deleteById(bpn);
    }

    @Test
    @DisplayName("Test deleteBpnEdcMapping with missing mapping")
    void testDeleteBpnEdcMappingWithMissingMapping() {
        String bpn = "12345";
        when(bpnEdcMappingRepositoryMock.exists(bpn)).thenReturn(false);
        Assertions.assertThrows(BpnEdcMappingNotFoundException.class, () -> {
            bpnEdcMappingService.deleteBpnEdcMapping(bpn);
        });
        verify(bpnEdcMappingRepositoryMock, never()).deleteById(bpn);
    }
}

