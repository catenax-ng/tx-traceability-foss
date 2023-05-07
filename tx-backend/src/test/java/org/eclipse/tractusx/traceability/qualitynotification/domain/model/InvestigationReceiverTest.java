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

package org.eclipse.tractusx.traceability.qualitynotification.domain.model;

import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.Investigation;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.Notification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationStatusTransitionNotAllowed;
import org.eclipse.tractusx.traceability.testdata.NotificationTestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationStatus.ACCEPTED;
import static org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationStatus.ACKNOWLEDGED;
import static org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationStatus.CANCELED;
import static org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationStatus.CLOSED;
import static org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationStatus.CREATED;
import static org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationStatus.DECLINED;
import static org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationStatus.RECEIVED;
import static org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationStatus.SENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class InvestigationReceiverTest {

    Investigation investigation;

    @Test
    @DisplayName("Forbid Acknowledge Investigation with disallowed status")
    void forbidAcknowledgeInvestigationWithStatusCreated() {

        // Given
        Notification notification = testNotification();
        InvestigationStatus status = CREATED;
        investigation = receiverInvestigationWithStatus(status);

        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.acknowledge(notification));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @Test
    @DisplayName("Forbid Acknowledge Investigation with status canceled")
    void forbidAcknowledgeInvestigationWithStatusCanceled() {

        // Given
        InvestigationStatus status = CANCELED;
        investigation = receiverInvestigationWithStatus(status);

        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.acknowledge(testNotification()));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @Test
    @DisplayName("Forbid Acknowledge Investigation with status accepted")
    void forbidAcknowledgeInvestigationWithStatusAccepted() {

        // Given
        InvestigationStatus status = ACCEPTED;
        investigation = receiverInvestigationWithStatus(status);

        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.acknowledge(testNotification()));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @Test
    @DisplayName("Forbid Acknowledge Investigation with status Declined")
    void forbidAcknowledgeInvestigationWithStatusDeclined() {

        // Given
        InvestigationStatus status = DECLINED;
        investigation = receiverInvestigationWithStatus(status);

        Notification notification = testNotification();
        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.acknowledge(notification));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @Test
    @DisplayName("Forbid Acknowledge Investigation with status Closed")
    void forbidAcknowledgeInvestigationWithStatusClosed() {

        // Given
        InvestigationStatus status = CLOSED;
        investigation = receiverInvestigationWithStatus(status);
        Notification notification = testNotification();
        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.acknowledge(notification));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @Test
    @DisplayName("Forbid Accept Investigation with status Closed")
    void forbidAcceptInvestigationWithStatusClosed() {

        // Given
        InvestigationStatus status = CLOSED;
        investigation = receiverInvestigationWithStatus(status);
        Notification notification = testNotification();
        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.accept("random reason", notification));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @Test
    @DisplayName("Forbid Accept Investigation with status sent")
    void forbidAcceptInvestigationWithStatusSent() {

        // Given
        InvestigationStatus status = SENT;
        investigation = receiverInvestigationWithStatus(status);
        Notification notification = testNotification();
        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.accept("random reason", notification));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @Test
    @DisplayName("Forbid Accept Investigation with status received")
    void forbidAcceptInvestigationWithStatusReceived() {

        // Given
        InvestigationStatus status = RECEIVED;
        investigation = receiverInvestigationWithStatus(status);
        Notification notification = testNotification();
        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.accept("random reason", notification));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @Test
    @DisplayName("Forbid Accept Investigation with status declined")
    void forbidAcceptInvestigationWithStatusDeclined() {

        // Given
        InvestigationStatus status = DECLINED;
        investigation = receiverInvestigationWithStatus(status);
        Notification notification = testNotification();
        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.accept("random reason", notification));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @Test
    @DisplayName("Forbid Acknowledge Investigation with status canceled")
    void forbidAcceptInvestigationWithStatusCanceled() {

        // Given
        InvestigationStatus status = CANCELED;
        investigation = receiverInvestigationWithStatus(status);
        Notification notification = testNotification();
        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.accept("random reason", notification));

        assertEquals(status, investigation.getInvestigationStatus());

    }


    @Test
    @DisplayName("Forbid Accept Investigation with disallowed status")
    void forbidAcceptInvestigationWithDisallowedStatus() {
        Notification notification = testNotification();

        InvestigationStatus status = CREATED;

        investigation = receiverInvestigationWithStatus(status);

        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.accept("some reason", notification));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @Test
    @DisplayName("Forbid Decline Investigation with disallowed status")
    void forbidDeclineInvestigationWithDisallowedStatus() {
        Notification notification = testNotification();

        InvestigationStatus status = CREATED;

        investigation = receiverInvestigationWithStatus(status);

        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.decline("some-reason", notification));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @Test
    @DisplayName("Forbid Decline Investigation with status sent")
    void forbidDeclineInvestigationWithStatusSent() {

        InvestigationStatus status = SENT;

        investigation = receiverInvestigationWithStatus(status);
        Notification notification = testNotification();
        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.decline("some-reason", notification));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @Test
    @DisplayName("Forbid Decline Investigation with status received")
    void forbidDeclineInvestigationWithStatusReceived() {

        InvestigationStatus status = RECEIVED;

        investigation = receiverInvestigationWithStatus(status);
        Notification notification = testNotification();

        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.decline("some-reason", notification));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @Test
    @DisplayName("Forbid Decline Investigation with status accepted")
    void forbidDeclineInvestigationWithStatusAccepted() {

        InvestigationStatus status = ACCEPTED;

        investigation = receiverInvestigationWithStatus(status);
        Notification notification = testNotification();
        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.decline("some-reason", notification));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @Test
    @DisplayName("Forbid Decline Investigation with status declined")
    void forbidDeclineInvestigationWithStatusDeclined() {

        InvestigationStatus status = DECLINED;

        investigation = receiverInvestigationWithStatus(status);
        Notification notification = testNotification();
        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.decline("some-reason", notification));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @Test
    @DisplayName("Forbid Decline Investigation with status canceled")
    void forbidDeclineInvestigationWithStatusCanceled() {

        InvestigationStatus status = CANCELED;

        investigation = receiverInvestigationWithStatus(status);
        Notification notification = testNotification();
        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.decline("some-reason", notification));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @Test
    @DisplayName("Forbid Decline Investigation with status closed")
    void forbidDeclineInvestigationWithStatusClosed() {

        InvestigationStatus status = CLOSED;

        investigation = receiverInvestigationWithStatus(status);
        Notification notification = testNotification();
        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.decline("some-reason", notification));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @Test
    @DisplayName("Forbid Close Investigation with status canceled")
    void forbidCloseInvestigationWithStatusCanceled() {

        InvestigationStatus status = CANCELED;

        investigation = receiverInvestigationWithStatus(status);
        BPN bpn = new BPN("BPNL000000000001");
        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.close(bpn, "some-reason"));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @Test
    @DisplayName("Forbid Close Investigation with status closed")
    void forbidCloseInvestigationWithStatusClosed() {

        InvestigationStatus status = CLOSED;

        investigation = receiverInvestigationWithStatus(status);
        BPN bpn = new BPN("BPNL000000000001");
        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.close(bpn, "some-reason"));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @Test
    @DisplayName("Forbid Send Investigation with status sent")
    void forbidSendInvestigationWithStatusSent() {

        InvestigationStatus status = SENT;

        investigation = receiverInvestigationWithStatus(status);
        BPN bpn = new BPN("BPNL000000000001");
        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.send(bpn));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @Test
    @DisplayName("Forbid Send Investigation with status received")
    void forbidSendInvestigationWithStatusReceived() {

        InvestigationStatus status = RECEIVED;

        investigation = receiverInvestigationWithStatus(status);
        BPN bpn = new BPN("BPNL000000000001");
        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.send(bpn));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @Test
    @DisplayName("Forbid Send Investigation with status acknowledged")
    void forbidSendInvestigationWithStatusAcknowledged() {

        InvestigationStatus status = ACKNOWLEDGED;

        investigation = receiverInvestigationWithStatus(status);
        BPN bpn = new BPN("BPNL000000000001");
        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.send(bpn));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @Test
    @DisplayName("Forbid Send Investigation with status accepted")
    void forbidSendInvestigationWithStatusAccepted() {

        InvestigationStatus status = ACCEPTED;

        investigation = receiverInvestigationWithStatus(status);

        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.send(new BPN("BPNL000000000001")));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @Test
    @DisplayName("Forbid Send Investigation with status declined")
    void forbidSendInvestigationWithStatusDeclined() {

        InvestigationStatus status = DECLINED;

        investigation = receiverInvestigationWithStatus(status);

        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.send(new BPN("BPNL000000000001")));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @Test
    @DisplayName("Forbid Send Investigation with status canceled")
    void forbidSendInvestigationWithStatusCanceled() {

        InvestigationStatus status = CANCELED;

        investigation = receiverInvestigationWithStatus(status);

        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.send(new BPN("BPNL000000000001")));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    @Test
    @DisplayName("Forbid Send Investigation with status closed")
    void forbidSendInvestigationWithStatusClosed() {

        InvestigationStatus status = CLOSED;

        investigation = receiverInvestigationWithStatus(status);

        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.send(new BPN("BPNL000000000001")));

        assertEquals(status, investigation.getInvestigationStatus());

    }

    private static Stream<Arguments> provideCancelInvalidStatus() {
        return Stream.of(
                Arguments.of(CANCELED),
                Arguments.of(CLOSED),
                Arguments.of(DECLINED),
                Arguments.of(ACCEPTED)

        );
    }

    ;

    @ParameterizedTest
    @DisplayName("Forbid Cancel Investigation with invalid statuses")
    @MethodSource("provideCancelInvalidStatus")
        //  @EnumSource(value = InvestigationStatus.class, names = {"CANCELED", "CLOSED", "DECLINED", "ACCEPTED", "ACKNOWLEDGED", "RECEIVED", "SENT" })
    void forbidCancelInvestigationWithInvalidStatus(InvestigationStatus status) {
        Investigation investigation = receiverInvestigationWithStatus(status);
        BPN bpn = new BPN("BPNL000000000001");
        assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.cancel(bpn));

        assertEquals(status, investigation.getInvestigationStatus());
    }


    @Test
    @DisplayName("Acknowledge Investigation successfully")
    void acknowledgeInvestigationSuccessfully() {
        Notification notification = testNotification();
        investigation = receiverInvestigationWithStatus(RECEIVED);
        investigation.acknowledge(notification);
        assertEquals(ACKNOWLEDGED, investigation.getInvestigationStatus());

    }

    @Test
    @DisplayName("Accept Investigation successfully")
    void acceptInvestigationSuccessfully() {
        Notification notification = testNotification();
        investigation = receiverInvestigationWithStatus(ACKNOWLEDGED);
        investigation.accept("some reason", notification);
        assertEquals(ACCEPTED, investigation.getInvestigationStatus());
        assertEquals(ACCEPTED, notification.getInvestigationStatus());
    }

    @Test
    @DisplayName("Decline Investigation successfully")
    void declineInvestigationSuccessfully() {
        Notification notification = testNotification();
        investigation = receiverInvestigationWithStatus(ACKNOWLEDGED);
        investigation.decline("some reason", notification);
        assertEquals(DECLINED, investigation.getInvestigationStatus());
        assertEquals(DECLINED, notification.getInvestigationStatus());
    }

    //util functions
    private Investigation receiverInvestigationWithStatus(InvestigationStatus status) {
        return investigationWithStatus(status, InvestigationSide.RECEIVER);
    }

    private Investigation investigationWithStatus(InvestigationStatus status, InvestigationSide side) {
        BPN bpn = new BPN("BPNL000000000001");
        return new Investigation(new InvestigationId(1L), bpn, status, side, "", "", "", "", Instant.now(), new ArrayList<>(), new ArrayList<>());
    }

    private Notification testNotification() {
        return NotificationTestDataFactory.createNotificationTestData();
    }
}
