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
import { Severity } from '@shared/model/severity.model';
import { SharedModule } from '@shared/shared.module';
import { screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

import { SeveritySelectComponent } from './severity-select.component';

describe('SeveritySelectComponent', () => {
  const renderSeveritySelect = (selectedValue?: Severity) => {
    return renderComponent(`<app-severity-select >Test</app-severity-select>`, {
      imports: [SharedModule],
      componentProperties: { selectedValue },
    });
  };

  it('should render selected Minor icon', async () => {
    await renderSeveritySelect();
    expect(screen.getByText('info')).toBeInTheDocument();
  });

  it('should render selected Major icon', async () => {
    await renderSeveritySelect(Severity.MAJOR);
    expect(screen.getByText('warning')).toBeInTheDocument();
  });

  it('should render selected Critical icon', async () => {
    await renderSeveritySelect(Severity.CRITICAL);
    expect(screen.getByText('error_outline')).toBeInTheDocument();
  });

  it('should render selected LifeThreatening icon', async () => {
    await renderSeveritySelect(Severity.LIFE_THREATENING);
    expect(screen.getByText('error')).toBeInTheDocument();
  });
});
