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

package org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.repository;

import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.model.AlertEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.QualityNotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.QualityNotificationStatusBaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaAlertRepository extends JpaRepository<AlertEntity, Long> {

    Page<AlertEntity> findAllBySideEqualsOrderByCreatedDateDesc(QualityNotificationSideBaseEntity investigationSide, Pageable pageable);

    long countAllByStatusEquals(QualityNotificationStatusBaseEntity status);

    long countAllBySideEquals(QualityNotificationSideBaseEntity alertSide);

    @Query("SELECT alert FROM AlertEntity alert JOIN alert.notifications notification WHERE notification.edcNotificationId = :edcNotificationId")
    Optional<AlertEntity> findByNotificationsEdcNotificationId(@Param("edcNotificationId") String edcNotificationId);
}
