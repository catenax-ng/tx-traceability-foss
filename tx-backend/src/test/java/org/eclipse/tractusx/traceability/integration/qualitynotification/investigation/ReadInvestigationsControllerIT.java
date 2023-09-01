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

package org.eclipse.tractusx.traceability.integration.qualitynotification.investigation;

import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.BpnSupport;
import org.eclipse.tractusx.traceability.integration.common.support.InvestigationNotificationsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.InvestigationsSupport;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationNotificationEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.QualityNotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.QualityNotificationStatusBaseEntity;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.eclipse.tractusx.traceability.integration.common.support.ISO8601DateTimeMatcher.isIso8601DateTime;

class ReadInvestigationsControllerIT extends IntegrationTestSpecification {

    @Autowired
    BpnSupport bpnSupport;
    @Autowired
    InvestigationNotificationsSupport investigationNotificationsSupport;
    @Autowired
    InvestigationsSupport investigationsSupport;

    @Test
    void shouldNotReturnCreatedInvestigationWithoutAuthentication() {
        given()
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/created")
                .then()
                .statusCode(401);
    }

    @Test
    void shouldNotReturnReceivedInvestigationWithoutAuthentication() {
        given()
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/received")
                .then()
                .statusCode(401);
    }

    @Test
    void shouldNotReturnInvestigationWithoutAuthentication() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/123")
                .then()
                .statusCode(401);
    }

    @Test
    void shouldReturnNoReceivedInvestigations() throws JoseException {
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/received")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(0));
    }

    @Test
    void shouldReturnNoCreatedInvestigations() throws JoseException {
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(0));
    }

    @Test
    void givenAlerts_whenGetAlerts_thenReturnSortedByCreationTime() throws JoseException {
        // given
        Instant now = Instant.now();
        String testBpn = bpnSupport.testBpn();

        InvestigationEntity firstInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(QualityNotificationStatusBaseEntity.CREATED)
                .side(QualityNotificationSideBaseEntity.SENDER)
                .description("1")
                .createdDate(now.minusSeconds(10L))
                .build();
        InvestigationEntity secondInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(QualityNotificationStatusBaseEntity.CREATED)
                .description("2")
                .side(QualityNotificationSideBaseEntity.SENDER)
                .createdDate(now.plusSeconds(21L))
                .build();
        InvestigationEntity thirdInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(QualityNotificationStatusBaseEntity.CREATED)
                .description("3")
                .side(QualityNotificationSideBaseEntity.SENDER)
                .createdDate(now)
                .build();
        InvestigationEntity fourthInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(QualityNotificationStatusBaseEntity.CREATED)
                .description("4")
                .side(QualityNotificationSideBaseEntity.SENDER)
                .createdDate(now.plusSeconds(20L))
                .build();
        InvestigationEntity fifthInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(QualityNotificationStatusBaseEntity.CREATED)
                .description("5")
                .side(QualityNotificationSideBaseEntity.RECEIVER)
                .createdDate(now.plusSeconds(40L))
                .build();

        investigationNotificationsSupport.storedNotifications(
                InvestigationNotificationEntity
                        .builder()
                        .id("1")
                        .investigation(firstInvestigation)
                        .status(QualityNotificationStatusBaseEntity.CREATED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .status(QualityNotificationStatusBaseEntity.CREATED)
                        .id("2")
                        .investigation(secondInvestigation)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .status(QualityNotificationStatusBaseEntity.CREATED)
                        .id("3")
                        .investigation(thirdInvestigation)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .status(QualityNotificationStatusBaseEntity.CREATED)
                        .id("4")
                        .investigation(fourthInvestigation)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .status(QualityNotificationStatusBaseEntity.CREATED)
                        .id("5")
                        .investigation(fifthInvestigation)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .build()
        );

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(4))
                .body("totalItems", Matchers.is(4));
    }

    @Test
    void givenSortProvided_whenGetAlerts_thenReturnAlertsProperlySorted() throws JoseException {
        // given
        String sortString = "createdDate,desc";
        Instant now = Instant.now();
        String testBpn = bpnSupport.testBpn();

        InvestigationEntity firstInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(QualityNotificationStatusBaseEntity.CREATED)
                .side(QualityNotificationSideBaseEntity.SENDER)
                .description("1")
                .createdDate(now.minusSeconds(10L))
                .build();
        InvestigationEntity secondInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(QualityNotificationStatusBaseEntity.CREATED)
                .description("2")
                .side(QualityNotificationSideBaseEntity.SENDER)
                .createdDate(now.plusSeconds(21L))
                .build();
        InvestigationEntity thirdInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(QualityNotificationStatusBaseEntity.CREATED)
                .description("3")
                .side(QualityNotificationSideBaseEntity.SENDER)
                .createdDate(now)
                .build();
        InvestigationEntity fourthInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(QualityNotificationStatusBaseEntity.CREATED)
                .description("4")
                .side(QualityNotificationSideBaseEntity.SENDER)
                .createdDate(now.plusSeconds(20L))
                .build();
        InvestigationEntity fifthInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(QualityNotificationStatusBaseEntity.CREATED)
                .description("5")
                .side(QualityNotificationSideBaseEntity.RECEIVER)
                .createdDate(now.plusSeconds(40L))
                .build();

        investigationNotificationsSupport.storedNotifications(
                InvestigationNotificationEntity
                        .builder()
                        .id("1")
                        .investigation(firstInvestigation)
                        .status(QualityNotificationStatusBaseEntity.CREATED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .status(QualityNotificationStatusBaseEntity.CREATED)
                        .id("2")
                        .investigation(secondInvestigation)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .status(QualityNotificationStatusBaseEntity.CREATED)
                        .id("3")
                        .investigation(thirdInvestigation)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .status(QualityNotificationStatusBaseEntity.CREATED)
                        .id("4")
                        .investigation(fourthInvestigation)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .status(QualityNotificationStatusBaseEntity.CREATED)
                        .id("5")
                        .investigation(fifthInvestigation)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .build()
        );

        expect:
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/created?page=0&size=10&sort=$sortString".replace("$sortString", sortString))
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(4))
                .body("totalItems", Matchers.is(4));
    }

    @Test
    void givenInvalidSort_whenGetCreated_thenBadRequest() throws JoseException {
        // given
        String sortString = "createdDate,failure";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/created?page=0&size=10&sort=$sortString".replace("$sortString", sortString))
                .then()
                .statusCode(400)
                .body("message", Matchers.is(
                        "Invalid sort param provided sort=createdDate,failure expected format is following sort=parameter,order"
                ));
    }

    @Test
    void shouldReturnPagedCreatedAlerts() throws JoseException {
        // given
        Instant now = Instant.now();
        String testBpn = bpnSupport.testBpn();

        IntStream.range(1, 101)
                .forEach(
                        number -> {
                            investigationsSupport.storedInvestigation(
                                    InvestigationEntity.builder()
                                            .assets(Collections.emptyList())
                                            .bpn(testBpn)
                                            .status(QualityNotificationStatusBaseEntity.CREATED)
                                            .side(QualityNotificationSideBaseEntity.SENDER)
                                            .createdDate(now)
                                            .build()
                            );
                        }
                );

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "2")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(2))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(10))
                .body("totalItems", Matchers.is(100));
    }

    @Test
    void shouldReturnProperlyPagedReceivedInvestigations() throws JoseException {
        // given
        Instant now = Instant.now();
        String testBpn = bpnSupport.testBpn();
        String senderBPN = "BPN0001";
        String senderName = "Sender name";
        String receiverBPN = "BPN0002";
        String receiverName = "Receiver name";

        IntStream.range(101, 201)
                .forEach(number ->
                        {
                            InvestigationEntity investigationEntity = InvestigationEntity.builder()
                                    .assets(Collections.emptyList())
                                    .bpn(testBpn)
                                    .status(QualityNotificationStatusBaseEntity.CREATED)
                                    .side(QualityNotificationSideBaseEntity.RECEIVER)
                                    .createdDate(now)
                                    .build();

                            InvestigationEntity investigation = investigationsSupport.storedInvestigationFullObject(investigationEntity);

                            InvestigationNotificationEntity notificationEntity = InvestigationNotificationEntity
                                    .builder()
                                    .id(UUID.randomUUID().toString())
                                    .investigation(investigation)
                                    .senderBpnNumber(senderBPN)
                                    .status(QualityNotificationStatusBaseEntity.CREATED)
                                    .senderManufacturerName(senderName)
                                    .receiverBpnNumber(receiverBPN)
                                    .receiverManufacturerName(receiverName)
                                    .messageId("messageId")
                                    .build();

                            InvestigationNotificationEntity persistedNotification = investigationNotificationsSupport.storedNotification(notificationEntity);
                            persistedNotification.setInvestigation(investigation);
                            investigationNotificationsSupport.storedNotification(persistedNotification);
                        }
                );

        expect:
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "2")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/received")
                .then()
                .statusCode(200)
                .body("content.createdBy", Matchers.hasItems(senderBPN))
                .body("content.createdByName", Matchers.hasItems(senderName))
                .body("content.sendTo", Matchers.hasItems(receiverBPN))
                .body("content.sendToName", Matchers.hasItems(receiverName))
                .body("page", Matchers.is(2))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(10))
                .body("totalItems", Matchers.is(100));
    }

    @Test
    void shouldReturnReceivedInvestigationsSortedByCreationTime() throws JoseException {
        // given
        Instant now = Instant.now();
        String testBpn = bpnSupport.testBpn();

        InvestigationEntity firstInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(QualityNotificationStatusBaseEntity.RECEIVED)
                .side(QualityNotificationSideBaseEntity.RECEIVER)
                .description("1")
                .createdDate(now.minusSeconds(5L))
                .build();
        InvestigationEntity secondInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(QualityNotificationStatusBaseEntity.RECEIVED)
                .description("2")
                .side(QualityNotificationSideBaseEntity.RECEIVER)
                .createdDate(now.plusSeconds(2L))
                .build();
        InvestigationEntity thirdInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(QualityNotificationStatusBaseEntity.RECEIVED)
                .description("3")
                .side(QualityNotificationSideBaseEntity.RECEIVER)
                .createdDate(now)
                .build();
        InvestigationEntity fourthInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(QualityNotificationStatusBaseEntity.RECEIVED)
                .description("4")
                .side(QualityNotificationSideBaseEntity.RECEIVER)
                .createdDate(now.plusSeconds(20L))
                .build();
        InvestigationEntity fifthInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(QualityNotificationStatusBaseEntity.CREATED)
                .description("5")
                .side(QualityNotificationSideBaseEntity.SENDER)
                .createdDate(now.plusSeconds(40L))
                .build();

        and:
        investigationNotificationsSupport.storedNotifications(
                InvestigationNotificationEntity
                        .builder()
                        .id("1")
                        .investigation(firstInvestigation)
                        .status(QualityNotificationStatusBaseEntity.CREATED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .id("2")
                        .investigation(secondInvestigation)
                        .status(QualityNotificationStatusBaseEntity.CREATED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .id("3")
                        .investigation(thirdInvestigation)
                        .status(QualityNotificationStatusBaseEntity.CREATED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .id("4")
                        .investigation(fourthInvestigation)
                        .status(QualityNotificationStatusBaseEntity.CREATED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .id("5")
                        .investigation(fifthInvestigation)
                        .status(QualityNotificationStatusBaseEntity.CREATED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .build()
        );

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/received")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(4))
                .body("totalItems", Matchers.is(4))
                .body("content.description", Matchers.containsInRelativeOrder("4", "2", "3", "1"))
                .body("content.createdDate", Matchers.hasItems(isIso8601DateTime()));
    }

    @Test
    void givenNoInvestigationId_whenGetInvestigationById_thenReturnNotFound() throws JoseException {
        expect:
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/1234")
                .then()
                .statusCode(404)
                .body("message", Matchers.is("Investigation not found for 1234 id"));
    }

    @Test
    void shouldReturnInvestigationById() throws JoseException {
        // given
        String testBpn = bpnSupport.testBpn();
        String senderBPN = "BPN0001";
        String senderName = "Sender name";
        String receiverBPN = "BPN0002";
        String receiverName = "Receiver name";

        InvestigationEntity investigationEntity =
                InvestigationEntity
                        .builder()
                        .id(1L)
                        .assets(List.of())
                        .bpn(testBpn)
                        .description("1")
                        .status(QualityNotificationStatusBaseEntity.RECEIVED)
                        .side(QualityNotificationSideBaseEntity.SENDER)
                        .createdDate(Instant.now())
                        .build();

        InvestigationEntity persistedInvestigation = investigationsSupport.storedInvestigationFullObject(investigationEntity);

        InvestigationNotificationEntity notificationEntity = investigationNotificationsSupport.storedNotification(
                InvestigationNotificationEntity
                        .builder()
                        .id("1")
                        .investigation(persistedInvestigation)
                        .senderBpnNumber(senderBPN)
                        .senderManufacturerName(senderName)
                        .receiverBpnNumber(receiverBPN)
                        .status(QualityNotificationStatusBaseEntity.CREATED)
                        .receiverManufacturerName(receiverName)
                        .build());
        notificationEntity.setInvestigation(persistedInvestigation);
        investigationNotificationsSupport.storedNotification(notificationEntity);
        Long investigationId = persistedInvestigation.getId();

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/{investigationId}", investigationId)
                .then()
                .statusCode(200)
                .body("id", Matchers.is(investigationId.intValue()))
                .body("status", Matchers.is("RECEIVED"))
                .body("description", Matchers.is("1"))
                .body("assetIds", Matchers.empty())
                .body("createdBy", Matchers.is(senderBPN))
                .body("createdByName", Matchers.is(senderName))
                .body("sendTo", Matchers.is(receiverBPN))
                .body("sendToName", Matchers.is(receiverName))
                .body("createdDate", isIso8601DateTime());
    }
}
