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

package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox

import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.http.ContentType
import org.eclipse.tractusx.traceability.IntegrationSpecification
import org.eclipse.tractusx.traceability.common.security.JwtRole
import org.eclipse.tractusx.traceability.common.support.AssetsSupport
import org.eclipse.tractusx.traceability.common.support.InvestigationsSupport
import org.eclipse.tractusx.traceability.common.support.NotificationsSupport
import org.eclipse.tractusx.traceability.common.support.TestDataSupport
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotification
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationStatus
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.base.QualityNotificationSideBaseEntity
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.base.QualityNotificationStatusBaseEntity
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationEntity
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationNotificationEntity
import org.springframework.beans.factory.annotation.Autowired

import java.time.Instant

import static io.restassured.RestAssured.given

class EdcControllerIT extends IntegrationSpecification implements TestDataSupport, AssetsSupport, NotificationsSupport, InvestigationsSupport {
    @Autowired
    private ObjectMapper objectMapper

    def "should create an investigation including notification on API callclass EdcControllerIT extends IntegrationSpecification implements TestDataSupport, AssetsSupport, NotificationsSupport, InvestigationsSupport {\nback /qualitynotifications/receive success"() {
        given:
        defaultAssetsStored()
        String notificationJson = readFile("edc_notification_okay.json")
        EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);

        when:
        given()
                .contentType(ContentType.JSON)
                .body(edcNotification)
                .header(jwtAuthorization(JwtRole.ADMIN))
                .when()
                .post("/api/qualitynotifications/receive")
                .then()
                .statusCode(200)

        then:
        assertNotificationsSize(1)
        assertInvestigationsSize(1)
        assertInvestigationStatus(QualityNotificationStatus.RECEIVED)
    }

    def "should not create an investigation on API callback /qualitynotifications/receive bad request bpn of notification does not match app bpn"() {
        given:
        String notificationJson = readFile("edc_notification_wrong_bpn.json")
        EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);
        when:
        given()
                .contentType(ContentType.JSON)
                .body(edcNotification)
                .header(jwtAuthorization(JwtRole.ADMIN))
                .when()
                .post("/api/qualitynotifications/receive")
                .then()
                .statusCode(400)

        then:
        assertNotificationsSize(0)
        assertInvestigationsSize(0)
    }

    def "should add a notification to an existing investigation on API callback /qualitynotifications/update success"() {
        given:
        defaultAssetsStored()
        InvestigationNotificationEntity notification = InvestigationNotificationEntity
                .builder()
                .id("1")
                .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                .status(QualityNotificationStatusBaseEntity.CREATED)
                .build()

        InvestigationEntity investigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn("BPNL00000003AXS3")
                .status(QualityNotificationStatusBaseEntity.SENT)
                .side(QualityNotificationSideBaseEntity.SENDER)
                .created(Instant.now())
                .build();

        InvestigationEntity persistedInvestigation = storedInvestigationFullObject(investigation)

        InvestigationNotificationEntity notificationEntity = storedNotification(notification)
        notificationEntity.setInvestigation(persistedInvestigation);
        InvestigationNotificationEntity persistedNotification = storedNotification(notificationEntity)

        investigation.setNotifications(List.of(persistedNotification))

        storedInvestigationFullObject(investigation)


        String notificationJson = readFile("edc_notification_okay_update.json").replaceAll("REPLACE_ME", notificationEntity.getEdcNotificationId())
        EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);


        when:
        given()
                .contentType(ContentType.JSON)
                .body(edcNotification)
                .header(jwtAuthorization(JwtRole.ADMIN))
                .when()
                .post("/api/qualitynotifications/update")
                .then()
                .statusCode(200)

        then:
        assertNotificationsSize(2)
        assertInvestigationsSize(1)

        assertInvestigationStatus(QualityNotificationStatus.ACKNOWLEDGED)
    }

    def "should throw bad request because edcNotification Method is not supported /qualitynotifications/receive"() {
        given:
        defaultAssetsStored()
        InvestigationNotificationEntity notification = InvestigationNotificationEntity
                .builder()
                .id("1")
                .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                .build()


        InvestigationEntity investigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn("BPNL00000003AXS3")
                .status(QualityNotificationStatusBaseEntity.SENT)
                .side(QualityNotificationSideBaseEntity.SENDER)
                .created(Instant.now())
                .build();

        InvestigationEntity persistedInvestigation = storedInvestigationFullObject(investigation)

        InvestigationNotificationEntity notificationEntity = storedNotification(notification)
        notificationEntity.setInvestigation(persistedInvestigation);
        InvestigationNotificationEntity persistedNotification = storedNotification(notificationEntity)

        investigation.setNotifications(List.of(persistedNotification))

        storedInvestigationFullObject(investigation)


        String notificationJson = readFile("edc_notification_classification_unsupported.json").replaceAll("REPLACE_ME", notificationEntity.getEdcNotificationId())
        EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);


        when:
        given()
                .contentType(ContentType.JSON)
                .body(edcNotification)
                .header(jwtAuthorization(JwtRole.ADMIN))
                .when()
                .post("/api/qualitynotifications/receive")
                .then()
                .statusCode(400)

        then:
        assertNotificationsSize(1)

    }

    def "should call the /qualitynotifications/update api with wrong requestobject "() {
        given:

        InvestigationEntity investigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn("BPNL00000003AXS3")
                .status(QualityNotificationStatusBaseEntity.RECEIVED)
                .side(QualityNotificationSideBaseEntity.RECEIVER)
                .created(Instant.now())
                .build();

        storedInvestigationFullObject(investigation)

        when:
        given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "\t\"header\": {\n" +
                        "\t\t\"notificationId\": \"notificationReferenceId\",\n" +
                        "\t\t\"senderBPN\": \"NOT_SAME_AS_APP_BPN\",\n" +
                        "\t\t\"senderAddress\": \"https://some-url.com\",\n" +
                        "\t\t\"recipientBPN\": \"NOT_SAME_AS_APP_BPN\",\n" +
                        "\t\t\"classification\": \"QM-Investigation\",\n" +
                        "\t\t\"severity\": \"CRITICAL\",\n" +
                        "\t\t\"relatedNotificationId\": \"\",\n" +
                        "\t\t\"status\": \"CLOSED\",\n" +
                        "\t\t\"targetDate\": \"\"\n" +
                        "\t},\n" +
                        "\t\"content\": {\n" +
                        "\t\t\"information\": \"Some long description\",\n" +
                        "\t\t\"listOfAffectedItems\": [\n" +
                        "\t\t\t\"urn:uuid:171fed54-26aa-4848-a025-81aaca557f37\"\n" +
                        "\t\t]\n" +
                        "\t}\n" +
                        "}")
                .header(jwtAuthorization(JwtRole.ADMIN))
                .when()
                .post("/api/qualitynotifications/update")
                .then()
                .statusCode(400)

        then:
        assertNotificationsSize(0)
        assertInvestigationsSize(1)
        assertInvestigationStatus(QualityNotificationStatus.RECEIVED)

    }
}
