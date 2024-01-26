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
package org.eclipse.tractusx.traceability.qualitynotification.domain.base.service;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

// TODO - either refactor this class to use feignClient with a common httpClient or remove it once IRS-Lib is done
@Slf4j
@Component
public class HttpCallService {

    private final OkHttpClient okhttpClient;

    @Autowired
    public HttpCallService(OkHttpClient okhttpClient) {
        this.okhttpClient = withIncreasedTimeout(okhttpClient);
    }

    private static OkHttpClient withIncreasedTimeout(OkHttpClient okhttpClient) {
        okhttpClient = new OkHttpClient();
        okhttpClient.setReadTimeout(25, TimeUnit.SECONDS);
        okhttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
        okhttpClient.setWriteTimeout(50, TimeUnit.SECONDS);
        okhttpClient.interceptors().add(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS));
        return okhttpClient;
    }


    public void sendRequest(Request request) throws IOException {
        try {
            var response = okhttpClient.newCall(request).execute();
            var body = response.body();
            if (!response.isSuccessful() || body == null) {
                throw new BadRequestException(format("Control plane responded with: %s %s", response.code(), body != null ? body.string() : ""));
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
