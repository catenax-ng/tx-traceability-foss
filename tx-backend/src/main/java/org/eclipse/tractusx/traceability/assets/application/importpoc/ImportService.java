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
// TODO package needs to be renamed (MW)
package org.eclipse.tractusx.traceability.assets.application.importpoc;


import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.ImportJob;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ImportService {
    Map<AssetBase, Boolean> importAssets(MultipartFile file, ImportJob importJob);

    ImportJob createJob();

    void completeJob(ImportJob importJob);

    void cancelJob(ImportJob importJob);

    ImportJob getImportJob(String importJobId);
}
