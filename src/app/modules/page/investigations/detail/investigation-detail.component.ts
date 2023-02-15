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

import { AfterViewInit, Component, OnDestroy, TemplateRef, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { InvestigationDetailFacade } from '@page/investigations/core/investigation-detail.facade';
import { InvestigationHelperService } from '@page/investigations/core/investigation-helper.service';
import { InvestigationsFacade } from '@page/investigations/core/investigations.facade';
import { Part } from '@page/parts/model/parts.model';
import { CtaSnackbarService } from '@shared/components/call-to-action-snackbar/cta-snackbar.service';
import { CreateHeaderFromColumns, TableConfig, TableEventConfig } from '@shared/components/table/table.model';
import { Notification, NotificationStatus } from '@shared/model/notification.model';
import { View } from '@shared/model/view.model';
import { AcceptNotificationModalComponent } from '@shared/modules/notification/modal/accept/accept-notification-modal.component';
import { AcknowledgeNotificationModalComponent } from '@shared/modules/notification/modal/acknowledge/acknowledge-notification-modal.component';
import { ApproveNotificationModalComponent } from '@shared/modules/notification/modal/approve/approve-notification-modal.component';
import { CancelNotificationModalComponent } from '@shared/modules/notification/modal/cancel/cancel-notification-modal.component';
import { CloseNotificationModalComponent } from '@shared/modules/notification/modal/close/close-notification-modal.component';
import { DeclineNotificationModalComponent } from '@shared/modules/notification/modal/decline/decline-notification-modal.component';
import { StaticIdService } from '@shared/service/staticId.service';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { filter, first, tap } from 'rxjs/operators';

@Component({
  selector: 'app-investigation-detail',
  templateUrl: './investigation-detail.component.html',
  styleUrls: ['./investigation-detail.component.scss'],
})
export class InvestigationDetailComponent implements AfterViewInit, OnDestroy {
  @ViewChild(ApproveNotificationModalComponent) approveModal: ApproveNotificationModalComponent;
  @ViewChild(CloseNotificationModalComponent) closeModal: CloseNotificationModalComponent;
  @ViewChild(CancelNotificationModalComponent) cancelModal: CancelNotificationModalComponent;

  @ViewChild(AcceptNotificationModalComponent) acceptModal: AcceptNotificationModalComponent;
  @ViewChild(AcknowledgeNotificationModalComponent) acknowledgeModal: AcknowledgeNotificationModalComponent;
  @ViewChild(DeclineNotificationModalComponent) declineModal: DeclineNotificationModalComponent;

  @ViewChild('serialNumberTmp') serialNumberTmp: TemplateRef<unknown>;

  public readonly investigationPartsInformation$: Observable<View<Part[]>>;
  public readonly supplierPartsDetailInformation$: Observable<View<Part[]>>;
  public readonly selected$: Observable<View<Notification>>;

  public readonly isInvestigationOpen$ = new BehaviorSubject<boolean>(false);
  public readonly selectedItems$ = new BehaviorSubject<Part[]>([]);
  public readonly deselectPartTrigger$ = new Subject<Part[]>();
  public readonly addPartTrigger$ = new Subject<Part>();

  public readonly notificationPartsTableId = this.staticIdService.generateId('InvestigationDetail');
  public readonly supplierPartsTableId = this.staticIdService.generateId('InvestigationDetail');

  public notificationPartsTableConfig: TableConfig;
  public supplierPartsTableConfig: TableConfig;
  public isReceived: boolean;

  private subscription: Subscription;
  private selectedInvestigationTmpStore: Notification;
  public selectedInvestigation: Notification;

  constructor(
    public readonly helperService: InvestigationHelperService,
    private readonly staticIdService: StaticIdService,
    private readonly investigationDetailFacade: InvestigationDetailFacade,
    private readonly investigationsFacade: InvestigationsFacade,
    private readonly route: ActivatedRoute,
    private readonly ctaSnackbarService: CtaSnackbarService,
  ) {
    this.investigationPartsInformation$ = this.investigationDetailFacade.notificationPartsInformation$;
    this.supplierPartsDetailInformation$ = this.investigationDetailFacade.supplierPartsInformation$;

    this.selected$ = this.investigationDetailFacade.selected$;
  }

  public ngAfterViewInit(): void {
    if (!this.investigationDetailFacade.selected?.data) {
      this.selectedNotificationBasedOnUrl();
    }

    this.subscription = this.selected$
      .pipe(
        filter(({ data }) => !!data),
        tap(({ data }) => {
          this.setTableConfigs(data);
          this.selectedInvestigation = data;
        }),
      )
      .subscribe();
  }

  public ngOnDestroy(): void {
    this.subscription?.unsubscribe();
    this.investigationDetailFacade.unsubscribeSubscriptions();
  }

  public onNotificationPartsSort({ sorting }: TableEventConfig): void {
    const [name, direction] = sorting || ['', ''];
    this.investigationDetailFacade.sortNotificationParts(name, direction);
  }

  public onSupplierPartsSort({ sorting }: TableEventConfig): void {
    const [name, direction] = sorting || ['', ''];
    this.investigationDetailFacade.sortSupplierParts(name, direction);
  }

  public onMultiSelect(event: unknown[]): void {
    this.selectedInvestigationTmpStore = Object.assign(this.investigationDetailFacade.selected);

    this.selectedItems$.next(event as Part[]);
  }

  public removeItemFromSelection(part: Part): void {
    this.deselectPartTrigger$.next([part]);
    this.selectedItems$.next(this.selectedItems$.getValue().filter(({ id }) => id !== part.id));
  }

  public clearSelected(): void {
    this.deselectPartTrigger$.next(this.selectedItems$.getValue());
    this.selectedItems$.next([]);
  }

  public addItemToSelection(part: Part): void {
    this.addPartTrigger$.next(part);
    this.selectedItems$.next([...this.selectedItems$.getValue(), part]);
  }

  public copyToClipboard(serialNumber: string): void {
    const text = { id: 'clipboard', values: { value: serialNumber } };
    navigator.clipboard.writeText(serialNumber).then(_ => this.ctaSnackbarService.show(text));
  }

  public handleConfirmActionCompletedEvent(): void {
    this.investigationDetailFacade.selected = { loader: true };
    this.subscription?.unsubscribe();
    this.ngAfterViewInit();
  }

  private setTableConfigs(data: Notification): void {
    this.isReceived = data.status === NotificationStatus.RECEIVED;

    const displayedColumns = ['id', 'name', 'serialNumber'];
    const sortableColumns = { id: true, name: true, serialNumber: true };

    const tableConfig = {
      displayedColumns,
      header: CreateHeaderFromColumns(displayedColumns, 'table.partsColumn'),
      sortableColumns: sortableColumns,
      hasPagination: false,
      cellRenderers: {
        serialNumber: this.serialNumberTmp,
      },
    };

    this.investigationDetailFacade.setInvestigationPartsInformation(data);
    this.notificationPartsTableConfig = { ...tableConfig };

    if (!this.isReceived) {
      return;
    }

    this.investigationDetailFacade.setAndSupplierPartsInformation();
    this.supplierPartsTableConfig = {
      ...tableConfig,
      displayedColumns: ['select', ...displayedColumns],
      header: CreateHeaderFromColumns(['select', ...displayedColumns], 'table.partsColumn'),
    };
  }

  private selectedNotificationBasedOnUrl(): void {
    const investigationId = this.route.snapshot.paramMap.get('investigationId');
    this.investigationsFacade
      .getInvestigation(investigationId)
      .pipe(
        first(),
        tap(notification => (this.investigationDetailFacade.selected = { data: notification })),
      )
      .subscribe();
  }
}
