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
package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MultivaluedMap;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.eclipse.edc.catalog.spi.Catalog;
import org.eclipse.edc.catalog.spi.CatalogRequest;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.jsontransformer.EdcTransformerTraceX;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation.NegotiationResponse;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.policy.AtomicConstraint;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.policy.LiteralExpression;
import org.eclipse.tractusx.traceability.infrastructure.edc.properties.EdcProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.transferprocess.TransferProcessRequest.DEFAULT_PROTOCOL;

@Slf4j
@Component
public class HttpCallService {

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final EdcProperties edcProperties;
    private final EdcTransformerTraceX edcTransformer;

    public HttpCallService(OkHttpClient httpClient, ObjectMapper objectMapper, EdcProperties edcProperties, EdcTransformerTraceX edcTransformer) {
        this.httpClient = withIncreasedTimeout(httpClient);
        this.objectMapper = objectMapper;
        this.edcProperties = edcProperties;
        this.edcTransformer = edcTransformer;
        objectMapper.registerSubtypes(AtomicConstraint.class, LiteralExpression.class);
    }

    private static OkHttpClient withIncreasedTimeout(OkHttpClient httpClient) {
        return httpClient.newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(25, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .build();
    }

    public Catalog getCatalogForNotification(
            String consumerEdcDataManagementUrl,
            String providerConnectorControlPlaneIDSUrl,
            Map<String, String> headers
    ) throws IOException {
        var url = consumerEdcDataManagementUrl + edcProperties.getCatalogPath();
        MediaType mediaType = MediaType.parse("application/json");

        // TODO instead of filtering with java logic after the call of this method we could add a filter to the .querySpec within CatalogRequest object. Max W.
        CatalogRequest catalogRequestDTO = CatalogRequest.Builder.newInstance()
                .protocol(DEFAULT_PROTOCOL)
                .providerUrl(providerConnectorControlPlaneIDSUrl)
                .build();

        final String requestJson = edcTransformer.transformCatalogRequestToJson(catalogRequestDTO).toString();
        var request = new Request.Builder().url(url).post(RequestBody.create(mediaType, requestJson));
        headers.forEach(request::addHeader);
        return sendCatalogRequest(request.build());
    }

    public Catalog sendCatalogRequest(Request request) throws IOException {
        log.info("Requesting {} {}...", request.method(), request.url());
        try (var response = httpClient.newCall(request).execute()) {
            var body = response.body();

            if (!response.isSuccessful() || body == null) {
                throw new BadRequestException(format("Control plane responded with: %s %s", response.code(), body != null ? body.string() : ""));
            }

            String res = body.string();
            return edcTransformer.transformCatalog(res, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw e;
        }
    }

    public NegotiationResponse sendNegotiationRequest(Request request) throws IOException {
        log.info("Requesting {} {}...", request.method(), request.url());
        try (var response = httpClient.newCall(request).execute()) {
            var body = response.body();

            if (!response.isSuccessful() || body == null) {
                throw new BadRequestException(format("Control plane responded with: %s %s", response.code(), body != null ? body.string() : ""));
            }

            String res = body.string();
            return edcTransformer.transformJsonToNegotiationResponse(res, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw e;
        }
    }

    public Object sendRequest(Request request, Class<?> responseObject) throws IOException {
        log.info("Requesting {} {}...", request.method(), request.url());
        try (var response = httpClient.newCall(request).execute()) {
            var body = response.body();

            if (!response.isSuccessful() || body == null) {
                throw new BadRequestException(format("Control plane responded with: %s %s", response.code(), body != null ? body.string() : ""));
            }

            String res = body.string();
            return objectMapper.readValue(res, responseObject);
        } catch (Exception e) {
            throw e;
        }
    }

    public void sendRequest(Request request) throws IOException {
        try (var response = httpClient.newCall(request).execute()) {
            var body = response.body();
            if (!response.isSuccessful() || body == null) {
                throw new BadRequestException(format("Control plane responded with: %s %s", response.code(), body != null ? body.string() : ""));
            }
        } catch (Exception e) {
            throw e;
        }
    }


    public HttpUrl getUrl(String connectorUrl, String subUrl, MultivaluedMap<String, String> parameters) {
        var url = connectorUrl;

        if (subUrl != null && !subUrl.isEmpty()) {
            url = url + "/" + subUrl;
        }

        HttpUrl.Builder httpBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();

        if (parameters == null) {
            return httpBuilder.build();
        }

        for (Map.Entry<String, List<String>> param : parameters.entrySet()) {
            for (String value : param.getValue()) {
                httpBuilder = httpBuilder.addQueryParameter(param.getKey(), value);
            }
        }

        return httpBuilder.build();
    }
}
