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

import { Component, Inject } from '@angular/core';
import {
  MAT_LEGACY_SNACK_BAR_DATA as MAT_SNACK_BAR_DATA,
  MatLegacySnackBar as MatSnackBar,
} from '@angular/material/legacy-snack-bar';
import { I18nMessage } from '@shared/model/i18n-message';
import { CallAction, CtaSnackbarData } from './cta-snackbar.model';

// CTA stands for call-to-action
@Component({
  selector: 'cta-snackbar',
  templateUrl: './cta-snackbar.component.html',
})
export class CtaSnackbarComponent {
  public get text(): I18nMessage {
    return this.data.text;
  }

  public get actions(): CallAction[] {
    return this.data.actions;
  }

  constructor(
    @Inject(MAT_SNACK_BAR_DATA)
    private readonly data: CtaSnackbarData,
    private readonly snackBar: MatSnackBar,
  ) {}

  public onActionClick(): void {
    this.snackBar.dismiss();
  }
}
