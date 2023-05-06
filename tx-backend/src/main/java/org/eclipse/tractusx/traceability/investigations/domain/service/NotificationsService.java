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
import org.eclipse.tractusx.traceability.assets.infrastructure.config.async.AssetsAsyncConfig;
import org.eclipse.tractusx.traceability.discovery.domain.model.Discovery;
import org.eclipse.tractusx.traceability.discovery.domain.service.DiscoveryService;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.InvestigationsEDCFacade;
import org.eclipse.tractusx.traceability.investigations.domain.model.Notification;
import org.eclipse.tractusx.traceability.investigations.domain.repository.InvestigationsRepository;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

import static org.slf4j.LoggerFactory.getLogger;

@RequiredArgsConstructor
@Service
public class NotificationsService {

    private final InvestigationsEDCFacade edcFacade;
    private final InvestigationsRepository repository;
    private final DiscoveryService discoveryService;

    private static final Logger logger = getLogger(MethodHandles.lookup().lookupClass());

    @Async(value = AssetsAsyncConfig.UPDATE_NOTIFICATION_EXECUTOR)
    public void asyncNotificationExecutor(Notification notification) {
        logger.info("::asyncNotificationExecutor::notification {}", notification);
        Discovery discovery = discoveryService.getDiscoveryByBPN(notification.getReceiverBpnNumber());
        String senderEdcUrl = discovery.getSenderUrl();

        discovery.getReceiverUrls()
                .forEach(receiverUrl -> {
                    edcFacade.startEDCTransfer(notification, receiverUrl, senderEdcUrl);
                    repository.update(notification);
                });
    }
}
