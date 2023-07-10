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

import { HttpClient } from '@angular/common/http';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-about',
  templateUrl: './about.component.html',
  styleUrls: ['./about.component.scss'],
})
export class AboutComponent {
  @Input() name: string;
  @Input() repositoryPath: string;
  @Input() license: string;
  @Input() licensePath: string;
  @Input() noticePath: string;
  @Input() sourcePath: string;
  @Input() commitId: string;

  constructor(private http: HttpClient) {
    this.name = "Traceability-foss";
    this.repositoryPath = "https://github.com/catenax-ng/tx-traceability-foss";
    this.license = "Apache-2.0";
    this.licensePath = "https://github.com/catenax-ng/tx-traceability-foss/blob/main/LICENSE";
    this.noticePath = "https://github.com/catenax-ng/tx-traceability-foss/blob/main/NOTICE.md";
    this.fetchAppInfo();

  }

   openLink(url: string) {
    window.open(url, '_blank')
  }

   fetchAppInfo() {
    this.http.get<any>('/assets/aboutInfo.json').subscribe(data => {
      this.sourcePath = data.sourcePath;
      this.commitId = data.commitId;
    })
  }
}
