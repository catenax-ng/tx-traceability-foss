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

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ErrorPageComponent } from './error-page.component';
import { renderComponent } from '@tests/test-render.utils';
import { DashboardComponent } from '@page/dashboard/presentation/dashboard.component';
import { DashboardModule } from '@page/dashboard/dashboard.module';
import { SharedModule } from '@shared/shared.module';
import { PartsModule } from '@page/parts/parts.module';
import { screen } from '@testing-library/angular';
import { ErrorPageModule } from '@page/error-page/error-page.module';

describe('ErrorPageComponent', () => {
  const renderErrorPageComponent = ({ roles = [] } = {}) =>
    renderComponent(ErrorPageComponent, {
      imports: [ErrorPageModule, SharedModule],
      translations: ['page.error-page'],
      roles,
    });

  it('should render header', async () => {
    await renderErrorPageComponent();

    expect(screen.getByText('Error page')).toBeInTheDocument();
  });
});
