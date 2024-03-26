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
package org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.service;

import org.eclipse.tractusx.traceability.qualitynotification.domain.notification.exception.NotificationNotFoundException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.notification.repository.NotificationRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.domain.notification.service.NotificationServiceImpl;
import org.eclipse.tractusx.traceability.testdata.InvestigationTestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvestigationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;
    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void testFindNotPresentInvestigationThrowsException() {
        // given
        when(notificationRepository.findOptionalQualityNotificationById(any(QualityNotificationId.class))).thenReturn(Optional.empty());

        // expect
        assertThrows(NotificationNotFoundException.class, () -> notificationService.find(0L));
    }

    @Test
    void testFindExistingInvestigation() {
        // given
        when(notificationRepository.findOptionalQualityNotificationById(any(QualityNotificationId.class))).thenReturn(Optional.of(
                InvestigationTestDataFactory.createInvestigationTestData(QualityNotificationStatus.ACKNOWLEDGED, QualityNotificationStatus.ACKNOWLEDGED)
        ));

        // expect
        QualityNotification investigation = notificationService.find(0L);

        // then
        assertThat(investigation).isNotNull();
    }

    @Test
    void testLoadNotPresentInvestigationThrowsException() {
        // given
        when(notificationRepository.findOptionalQualityNotificationById(any(QualityNotificationId.class))).thenReturn(Optional.empty());

        // expect
        QualityNotificationId investigationId = new QualityNotificationId(0L);
        assertThrows(NotificationNotFoundException.class, () -> notificationService.loadOrNotFoundException(investigationId));
    }

    @Test
    void testLoadExistingInvestigation() {
        // given
        when(notificationRepository.findOptionalQualityNotificationById(any(QualityNotificationId.class))).thenReturn(Optional.of(
                InvestigationTestDataFactory.createInvestigationTestData(QualityNotificationStatus.ACKNOWLEDGED, QualityNotificationStatus.ACKNOWLEDGED)
        ));

        // expect
        QualityNotification investigation = notificationService.loadOrNotFoundException(new QualityNotificationId(0L));

        // then
        assertThat(investigation).isNotNull();
    }

    @Test
    void testLoadNotPresentInvestigationByEdcNotificationIdThrowsException() {
        // given
        when(notificationRepository.findByEdcNotificationId(any())).thenReturn(Optional.empty());

        // expect
        assertThrows(NotificationNotFoundException.class, () -> notificationService.loadByEdcNotificationIdOrNotFoundException("0"));
    }

    @Test
    void testLoadPresentInvestigationByEdcNotificationId() {
        // given
        when(notificationRepository.findByEdcNotificationId(any())).thenReturn(Optional.of(
                        InvestigationTestDataFactory.createInvestigationTestData(QualityNotificationStatus.ACKNOWLEDGED, QualityNotificationStatus.ACKNOWLEDGED)
                )
        );

        // when
        QualityNotification investigation = notificationService.loadByEdcNotificationIdOrNotFoundException("0");

        // then
        assertThat(investigation).isNotNull();
    }
}
