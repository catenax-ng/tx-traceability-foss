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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.List;

@Component
public class DatabaseSupport {
    private static final List<String> TABLES = List.of(new String[]{
            "submodel_payload",
            "import_job_assets_as_built",
            "import_job_assets_as_planned",
            "assets_as_built_childs",
            "assets_as_built_parents",
            "assets_as_built_notifications",
            "assets_as_built_investigations",
            "asset_as_built_alert_notifications",
            "asset_as_planned_alert_notifications",
            "assets_as_built_alerts",
            "assets_as_planned_childs",
            "assets_as_planned_notifications",
            "assets_as_planned_investigations",
            "assets_as_planned_alerts",
            "alert_notification",
            "alert",
            "assets_as_built",
            "assets_as_planned",
            "bpn_storage",
            "investigation_notification",
            "investigation",
            "traction_battery_code_subcomponent",
            "import_job"
    });


    @Autowired
    JdbcTemplate jdbcTemplate;

    /* This will be called after each test method has been executed. */
    public void clearAllTables() {
        TABLES.forEach(table -> {
            JdbcTestUtils.deleteFromTables(jdbcTemplate, table);
        });
    }

}
