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

package org.eclipse.tractusx.traceability.integration.common.support;

import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.model.AlertEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.repository.JpaAlertRepository;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.QualityNotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.QualityNotificationStatusBaseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class AlertsSupport {

    @Autowired
    JpaAlertRepository jpaAlertRepository;

    public Long defaultReceivedAlertStored() {
        AlertEntity entity = AlertEntity.builder()
                .assets(Collections.emptyList())
                .bpn("BPNL00000003AXS3")
                .status(QualityNotificationStatusBaseEntity.RECEIVED)
                .side(QualityNotificationSideBaseEntity.RECEIVER)
                .description("some description")
                .createdDate(Instant.now())
                .build();

        return storedAlert(entity);
    }

    Long defaultAcknowledgedAlertStored() {
        AlertEntity entity = AlertEntity.builder()
                .assets(Collections.emptyList())
                .bpn("BPNL00000003AXS3")
                .status(QualityNotificationStatusBaseEntity.ACKNOWLEDGED)
                .side(QualityNotificationSideBaseEntity.RECEIVER)
                .createdDate(Instant.now())
                .build();

        return storedAlert(entity);
    }

    public void assertAlertsSize(int size) {
        List<AlertEntity> alerts = jpaAlertRepository.findAll();

        assert alerts.size() == size;
    }

    public void assertAlertStatus(QualityNotificationStatus alertStatus) {
        jpaAlertRepository.findAll().stream().forEach(alert -> {
            assert alert.getStatus().name() == alertStatus.name();
        });
    }

    void storedAlerts(AlertEntity... alerts) {
        jpaAlertRepository.saveAll(Arrays.asList(alerts));
    }

    public Long storedAlert(AlertEntity alert) {
        return jpaAlertRepository.save(alert).getId();
    }

    public AlertEntity storedAlertFullObject(AlertEntity alert) {
        return jpaAlertRepository.save(alert);
    }
}
