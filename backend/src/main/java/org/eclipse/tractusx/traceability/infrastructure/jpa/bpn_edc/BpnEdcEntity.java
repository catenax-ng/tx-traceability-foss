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

package org.eclipse.tractusx.traceability.infrastructure.jpa.bpn_edc;

import jakarta.persistence.*;

@Entity
@Table(name = "bpn_edc_mappings")
@IdClass(BpnEdcId.class)
public class BpnEdcEntity {

    @Id
    private String bpn;

    @Id
    @Column(name = "edc_url")
    private String url;

    public BpnEdcEntity() {
    }

    public BpnEdcEntity(BpnEdcId id) {
        this.bpn = id.getBpn();
        this.url = id.getUrl();
    }

    public BpnEdcEntity(String bpn, String url) {
        this.bpn = bpn;
        this.url = url;
    }

    public String getBpn() {
        return bpn;
    }

    public void setBpn(String bpn) {
        this.bpn = bpn;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bpn == null) ? 0 : bpn.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BpnEdcEntity other = (BpnEdcEntity) obj;
        if (bpn == null) {
            if (other.bpn != null)
                return false;
        } else if (!bpn.equals(other.bpn))
            return false;
        if (url == null) {
            return other.url == null;
        } else return url.equals(other.url);
    }

}
