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
import { View } from '@shared/model/view.model';
import { InvestigationsService } from '@shared/service/investigations.service';
import { PartsService } from '@shared/service/parts.service';
import { Observable, Subscription } from 'rxjs';
import { DashboardService } from '../core/dashboard.service';
import { DashboardState } from '../core/dashboard.state';
import { DashboardStats } from '../model/dashboard.model';

@Injectable()
export class DashboardFacade {
  private assetNumbersSubscription: Subscription;
  private investigationSubscription: Subscription;

  constructor(
    private readonly dashboardService: DashboardService,
    private readonly dashboardState: DashboardState,
    private readonly partsService: PartsService,
    private readonly investigationsService: InvestigationsService,
  ) {}

  public get numberOfMyParts$(): Observable<View<number>> {
    return this.dashboardState.numberOfMyParts$;
  }

  public get numberOfOtherParts$(): Observable<View<number>> {
    return this.dashboardState.numberOfOtherParts$;
  }

  public get numberOfInvestigations$(): Observable<View<number>> {
    return this.dashboardState.numberOfInvestigations$;
  }

  public get investigations$(): Observable<View<Notifications>> {
    return this.dashboardState.investigations$;
  }

  public setDashboardData(): void {
    this.setAssetNumbers();
    this.setInvestigations();
  }

  private setAssetNumbers(): void {
    this.dashboardState.setNumberOfMyParts({ loader: true });
    this.dashboardState.setNumberOfOtherParts({ loader: true });
    this.dashboardState.setNumberOfInvestigations({ loader: true });

    this.assetNumbersSubscription?.unsubscribe();
    this.assetNumbersSubscription = this.dashboardService.getStats().subscribe({
      next: (dashboardStats: DashboardStats) => {
        this.dashboardState.setNumberOfMyParts({ data: dashboardStats.myItems });
        this.dashboardState.setNumberOfOtherParts({ data: dashboardStats.otherParts });
        this.dashboardState.setNumberOfInvestigations({ data: dashboardStats.investigations || 0 });
      },
      error: error => {
        this.dashboardState.setNumberOfMyParts({ error });
        this.dashboardState.setNumberOfOtherParts({ error });
        this.dashboardState.setNumberOfInvestigations({ error });
      },
    });
  }

  public stopDataLoading(): void {
    this.assetNumbersSubscription?.unsubscribe();
    this.investigationSubscription?.unsubscribe();
  }

  private setInvestigations(): void {
    this.investigationSubscription?.unsubscribe();
    this.investigationSubscription = this.investigationsService.getReceivedInvestigations(0, 5).subscribe({
      next: data => this.dashboardState.setInvestigation({ data }),
      error: (error: Error) => this.dashboardState.setInvestigation({ error }),
    });
  }
}
