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

import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { authCodeFlowConfig } from '@core/auth/keycloak.helper';
import { environment } from '@env';
import { OAuthService } from 'angular-oauth2-oidc';
import { filter } from 'rxjs/operators';
import * as mockService from '../../../mocks/mock';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
  constructor(private readonly router: Router, private readonly oauthService: OAuthService) {
    this.configureCodeFlow();

    // Automatically load user profile
    this.oauthService.events.pipe(filter(e => e.type === 'token_received')).subscribe(_ => {
      console.debug('state', this.oauthService.state);
      void this.oauthService.loadUserProfile();

      const scopes = this.oauthService.getGrantedScopes();
      console.debug('scopes', scopes);
    });

    if (environment.mockService) void mockService.worker.start({ onUnhandledRequest: 'bypass' });
  }

  private async configureCodeFlow() {
    this.oauthService.configure(authCodeFlowConfig);
    void this.oauthService.loadDiscoveryDocumentAndTryLogin();

    this.oauthService.setupAutomaticSilentRefresh();
  }
}
