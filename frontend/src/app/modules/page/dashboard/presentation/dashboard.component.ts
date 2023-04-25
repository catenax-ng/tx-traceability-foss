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

import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { getRoute, INVESTIGATION_BASE_ROUTE } from '@core/known-route';
import { Notification, Notifications, NotificationStatusGroup } from '@shared/model/notification.model';
import { View } from '@shared/model/view.model';
import { CloseNotificationModalComponent } from '@shared/modules/notification/modal/close/close-notification-modal.component';
import { Observable } from 'rxjs';
import { DashboardFacade } from '../abstraction/dashboard.facade';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent implements OnInit, OnDestroy {
  @ViewChild(CloseNotificationModalComponent) private closeModal: CloseNotificationModalComponent;

  public readonly numberOfMyParts$: Observable<View<number>>;
  public readonly numberOfOtherParts$: Observable<View<number>>;
  public readonly numberOfInvestigations$: Observable<View<number>>;

  public readonly investigations$: Observable<View<Notifications>>;

  public readonly investigationLink: string;
  public readonly investigationParams: Record<string, string>;

  constructor(private readonly dashboardFacade: DashboardFacade, private readonly router: Router) {
    this.numberOfMyParts$ = this.dashboardFacade.numberOfMyParts$;
    this.numberOfOtherParts$ = this.dashboardFacade.numberOfOtherParts$;
    this.numberOfInvestigations$ = this.dashboardFacade.numberOfInvestigations$;

    this.investigations$ = this.dashboardFacade.investigations$;

    const { link, queryParams } = getRoute(INVESTIGATION_BASE_ROUTE, NotificationStatusGroup.RECEIVED);
    this.investigationLink = link;
    this.investigationParams = queryParams;
  }

  public ngOnInit(): void {
    this.dashboardFacade.setDashboardData();
  }

  public ngOnDestroy(): void {
    this.dashboardFacade.stopDataLoading();
  }

  public onNotificationSelected(notification: Notification): void {
    const { link } = getRoute(INVESTIGATION_BASE_ROUTE);
    this.router.navigate([`/${link}/${notification.id}`]).then();
  }
}
