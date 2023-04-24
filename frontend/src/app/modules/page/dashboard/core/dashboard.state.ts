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

import { Injectable } from '@angular/core';

import { Notifications } from '@shared/model/notification.model';
import { State } from '@shared/model/state';
import { Observable } from 'rxjs';
import { View } from 'src/app/modules/shared/model/view.model';

@Injectable()
export class DashboardState {
  private readonly _numberOfMyParts$: State<View<number>> = new State<View<number>>({ loader: true });
  private readonly _numberOfOtherParts$: State<View<number>> = new State<View<number>>({ loader: true });
  private readonly _numberOfInvestigations$: State<View<number>> = new State<View<number>>({ loader: true });

  private readonly _investigations$: State<View<Notifications>> = new State<View<Notifications>>({ loader: true });

  public get numberOfMyParts$(): Observable<View<number>> {
    return this._numberOfMyParts$.observable;
  }

  public setNumberOfMyParts(count: View<number>): void {
    this._numberOfMyParts$.update(count);
  }

  public get numberOfOtherParts$(): Observable<View<number>> {
    return this._numberOfOtherParts$.observable;
  }

  public setNumberOfOtherParts(count: View<number>): void {
    this._numberOfOtherParts$.update(count);
  }

  public get numberOfInvestigations$(): Observable<View<number>> {
    return this._numberOfInvestigations$.observable;
  }

  public setNumberOfInvestigations(count: View<number>): void {
    this._numberOfInvestigations$.update(count);
  }

  public get investigations$(): Observable<View<Notifications>> {
    return this._investigations$.observable;
  }

  public setInvestigation(investigations: View<Notifications>): void {
    this._investigations$.update(investigations);
  }
}
