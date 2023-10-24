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

package org.eclipse.tractusx.traceability.integration.assets;

import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository.JpaAssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.hamcrest.Matchers.equalTo;

class AssetAsBuiltControllerFilteringIT extends IntegrationTestSpecification {

    @Autowired
    AssetsSupport assetsSupport;

    @Autowired
    JpaAssetAsBuiltRepository repo;

    @Test
    void givenNoFilter_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built")
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(13));
    }

    @Test
    void givenNoFilter_whenCallFilteredEndpointWithoutOperator_thenReturnBadRequest() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        final String filter = "?filter=activeAlert,EQUAL,false";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built" + filter)
                .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    void givenInInvestigationFalseFilter_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        final String filter = "?filter=activeAlert,EQUAL,false,AND";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built" + filter)
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(13));
    }

    @Test
    void givenInInvestigationTrueFilter_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        final String filter = "?filter=activeAlert,EQUAL,true,AND";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built" + filter)
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(0));
    }

    @Test
    void givenIdAndIdShortFilter_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        final String filter = "?filter=id,STARTS_WITH,urn:uuid:1,AND&filter=idShort,STARTS_WITH,engineering,AND";
        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built" + filter)
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(2));
    }

    @Test
    void givenOwnFilter_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        final String filter = "?filter=owner,EQUAL,OWN,AND";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built" + filter)
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(1));
    }

    @Test
    void givenManufacturerIdFilter_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        final String filter = "?filter=manufacturerId,EQUAL,BPNL00000003B0Q0,AND";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built" + filter)
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(4));
    }

    @Test
    void givenManufacturerIdAndSemanticModelIdFilter_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        final String filter = "?filter=manufacturerId,EQUAL,BPNL00000003B0Q0,AND&filter=semanticModelId,STARTS_WITH,NO-3404609481920549,AND";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built" + filter)
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(1));
    }

    @Test
    void givenIdShortStartsWithFilter_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        final String filter = "?filter=idShort,STARTS_WITH,ntier_,AND";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built" + filter)
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(1));
    }

    @Test
    void givenManufacturingDateFilter_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        final String filter = "?filter=manufacturingDate,AT_LOCAL_DATE,2014-11-18,AND";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built" + filter)
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(1));
    }

    @Test
    void givenSemanticDataModelAndManufacturingDateFilterOR_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        final String filter = "?filter=manufacturingDate,AT_LOCAL_DATE,2014-11-18,OR&filter=semanticDataModel,EQUAL,SERIALPART,OR";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built" + filter)
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(13));
    }

    @Test
    void givenSemanticDataModelAndManufacturingDateFilterAnd_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        final String filter = "?filter=manufacturingDate,AT_LOCAL_DATE,2014-11-18,AND&filter=semanticDataModel,EQUAL,SERIALPART,AND";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built" + filter)
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(1));
    }

    @Test
    void givenSemanticDataModelAndOwnerOR_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        final String filter = "?filter=owner,EQUAL,SUPPLIER,AND&filter=id,STARTS_WITH,urn:uuid:f7cf62fe-9e25-472b-9148-66,OR";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built" + filter)
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(1));
    }

    @Test
    void givenSemanticDataModelAsMultipleValuesAndOwnerOR_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        final String filter = "?filter=owner,EQUAL,SUPPLIER,AND&filter=semanticDataModel,EQUAL,SERIALPART,OR&filter=semanticDataModel,EQUAL,BATCH,OR";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built" + filter)
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(12)).extract().response();
    }
}
