/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

import { Component, OnDestroy, TemplateRef, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { getRoute, NOTIFICATION_BASE_ROUTE } from '@core/known-route';
import { Pagination } from '@core/model/pagination.model';
import { DEFAULT_PAGE_SIZE, FIRST_PAGE } from '@core/pagination/pagination.model';
import { NotificationDetailFacade } from '@page/notifications/core/notification-detail.facade';
import { NotificationsFacade } from '@page/notifications/core/notifications.facade';
import { SharedPartService } from '@page/notifications/detail/edit/shared-part.service';
import { OtherPartsFacade } from '@page/other-parts/core/other-parts.facade';
import { PartsFacade } from '@page/parts/core/parts.facade';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { AssetAsBuiltFilter, Part } from '@page/parts/model/parts.model';
import { NotificationActionHelperService } from '@shared/assembler/notification-action-helper.service';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { NotificationCommonModalComponent } from '@shared/components/notification-common-modal/notification-common-modal.component';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { ToastService } from '@shared/components/toasts/toast.service';
import { toAssetFilter } from '@shared/helper/filter-helper';
import { Notification, NotificationType } from '@shared/model/notification.model';
import { View } from '@shared/model/view.model';
import { StaticIdService } from '@shared/service/staticId.service';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';

@Component({
  selector: 'app-notification-edit',
  templateUrl: './notification-edit.component.html',
  styleUrls: [ './notification-edit.component.scss' ],
})
export class NotificationEditComponent implements OnDestroy {
  @ViewChild(NotificationCommonModalComponent) notificationCommonModalComponent: NotificationCommonModalComponent;

  @ViewChild('semanticModelIdTmp') semanticModelIdTmp: TemplateRef<unknown>;

  public readonly affectedPartsTableLabelId = this.staticIdService.generateId('AffectedPartsTable');
  public readonly availablePartsTableLabelId = this.staticIdService.generateId('AvailablePartsTable');

  public readonly deselectPartTrigger$ = new Subject<Part[]>();

  public readonly editMode: boolean = true;
  public notificationFormGroup: FormGroup;

  public affectedPartIds: string[] = this.sharedPartService?.affectedParts?.map(value => value.id) || [];
  public temporaryAffectedParts: Part[] = [];
  public temporaryAffectedPartsForRemoval: Part[] = [];
  public readonly currentSelectedAvailableParts$ = new BehaviorSubject<Part[]>([]);
  public readonly currentSelectedAffectedParts$ = new BehaviorSubject<Part[]>([]);
  public availablePartsAsBuilt$: Observable<View<Pagination<Part>>>;
  public affectedPartsAsBuilt$: Observable<View<Pagination<Part>>>;

  public cachedAffectedPartsFilter: string;
  public cachedAvailablePartsFilter: string;

  private originPageNumber: number;
  private originTabIndex: number;

  public selectedNotification: Notification;
  public tableType: TableType;
  public tableAsBuiltSortList: TableHeaderSort[];
  private paramSubscription: Subscription;
  isSaveButtonDisabled: boolean;

  constructor(
    private readonly partsFacade: OtherPartsFacade,
    private readonly ownPartsFacade: PartsFacade,
    public readonly actionHelperService: NotificationActionHelperService,
    public readonly notificationDetailFacade: NotificationDetailFacade,
    private readonly staticIdService: StaticIdService,
    private readonly notificationsFacade: NotificationsFacade,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly toastService: ToastService,
    private readonly sharedPartService: SharedPartService,
  ) {
    this.editMode = this.determineEditModeOrCreateMode();

    this.currentSelectedAvailableParts$.subscribe((parts: Part[]) => {
      this.temporaryAffectedParts = parts;
    });

    this.currentSelectedAffectedParts$.subscribe((parts: Part[]) => {
      this.temporaryAffectedPartsForRemoval = parts;
    });

    this.paramSubscription = this.route.queryParams.subscribe(params => {
      this.originPageNumber = params.pageNumber;
      this.originTabIndex = params?.tabIndex;
    });

    if (this.editMode) {
      this.isSaveButtonDisabled = true;
      this.handleEditNotification();
    } else {
      this.isSaveButtonDisabled = false;
      this.handleCreateNotification();
    }
  }

  private handleEditNotification() {
    if (this.notificationDetailFacade.selected?.data) {
      this.tableType = this.notificationDetailFacade.selected.data.type === NotificationType.INVESTIGATION ? TableType.AS_BUILT_SUPPLIER : TableType.AS_BUILT_OWN;

      this.selectNotificationAndLoadPartsBasedOnNotification(this.notificationDetailFacade.selected.data);
    } else {
      this.getNotificationByIdAndSelectNotificationAndLoadPartsBasedOnNotification();

    }
  }

  private handleCreateNotification() {

    this.isSaveButtonDisabled = true;
    const newNotification: Notification = {
      assetIds: this.sharedPartService?.affectedParts?.map(value => value.id) || [],
      createdBy: '',
      type: this.route.snapshot.queryParams['initialType'] ?? null,
      createdByName: '',
      createdDate: undefined,
      description: '',
      isFromSender: true,
      reason: undefined,
      sendTo: '',
      sendToName: '',
      severity: undefined,
      status: undefined,
      title: '',
      id: 'new',
    };
    this.selectNotificationAndLoadPartsBasedOnNotification(newNotification);
  }

  private determineEditModeOrCreateMode() {
    const urlPartIndex = this.route.snapshot.url?.length !== null ? this.route.snapshot.url.length - 1 : null;
    if (urlPartIndex) {
      return this.route.snapshot.url[urlPartIndex].path === 'edit';
    } else {
      return false;
    }
  }

  public notificationFormGroupChange(notificationFormGroup: FormGroup) {
    // if user switches type of notification in creation mode, reset affected parts and reload new available parts
    if (this.selectedNotification.type !== notificationFormGroup.value['type']) {
      this.selectedNotification.type = notificationFormGroup.value['type'];
      // TODO: comment back in if todos inside the function were handled
      // this.switchSelectedNotificationTypeAndResetParts();
    }

    this.notificationFormGroup = notificationFormGroup;
    this.isSaveButtonDisabled = (notificationFormGroup.invalid || this.affectedPartIds.length < 1) || !this.notificationFormGroup.dirty;
    if (this.notificationFormGroup && this.notificationFormGroup.get('type').value === NotificationType.INVESTIGATION.valueOf() && !this.notificationFormGroup.get('bpn').value && this.sharedPartService.affectedParts && this.sharedPartService.affectedParts.length > 0) {
      this.notificationFormGroup.get('bpn').setValue(this.sharedPartService.affectedParts[0].businessPartner);
    }
  }

  filterAffectedParts(partsFilter: any): void {
    if (this.cachedAffectedPartsFilter === JSON.stringify(partsFilter)) {
      return;
    } else {
      this.cachedAffectedPartsFilter = JSON.stringify(partsFilter);
      this.setAffectedPartsBasedOnNotificationType(this.selectedNotification, partsFilter);
    }
  }

  private enrichPartsFilterByAffectedAssetIds(partsFilter: any, exclude?: boolean) {

    let filter: AssetAsBuiltFilter = {
      excludeIds: [],
      ids: [],
      ...partsFilter,

    };

    if (exclude) {
      filter.excludeIds = this.affectedPartIds;
    } else {
      filter.ids = this.affectedPartIds;
    }
    return filter;

  }

  paginationChangedAffectedParts(event: any){
    this.setAffectedPartsBasedOnNotificationType(this.selectedNotification, this.cachedAffectedPartsFilter, event);
  }

  paginationChangedAvailableParts(event: any){
    this.setAvailablePartsBasedOnNotificationType(this.selectedNotification, this.cachedAvailablePartsFilter, event);
  }

  filterAvailableParts(partsFilter: any): void {
    if (this.cachedAvailablePartsFilter !== JSON.stringify(partsFilter)) {
      this.setAvailablePartsBasedOnNotificationType(this.selectedNotification, partsFilter);
    }
  }

  public clickedSave(): void {
    const { title, type, description, severity, targetDate, bpn } = this.notificationFormGroup.getRawValue();
    if (this.editMode) {
      this.notificationsFacade.editNotification(this.selectedNotification.id, title, bpn, severity, targetDate, description, this.affectedPartIds).subscribe({
        next: () => {
          this.navigateBackToNotifications();
          this.toastService.success('requestNotification.saveEditSuccess');
          this.updateSelectedNotificationState();
        },
        error: () => this.toastService.error('requestNotification.saveEditError'),
      });
    } else {
      this.notificationsFacade.createNotification(this.affectedPartIds, type, title, bpn, severity, targetDate, description).subscribe({
        next: () => {
          this.toastService.success('requestNotification.saveSuccess');
          this.navigateBackToNotifications();
          this.updateSelectedNotificationState();
        },
        error: () => this.toastService.error('requestNotification.saveError'),
      });
    }
  }


  private setAvailablePartsBasedOnNotificationType(notification: Notification, assetFilter?: any, pagination?: any) {
    if (this.affectedPartIds) {
      assetFilter = this.enrichPartsFilterByAffectedAssetIds(null, true);
    }
    if (notification.type === NotificationType.INVESTIGATION) {
      this.partsFacade.setSupplierPartsAsBuilt(pagination?.page || FIRST_PAGE, pagination?.pageSize || DEFAULT_PAGE_SIZE, this.tableAsBuiltSortList, toAssetFilter(assetFilter, true));
    } else {
      this.ownPartsFacade.setPartsAsBuilt(pagination?.page || FIRST_PAGE, pagination?.pageSize || DEFAULT_PAGE_SIZE, this.tableAsBuiltSortList, toAssetFilter(assetFilter, true));
    }
  }

  private setAffectedPartsBasedOnNotificationType(notification: Notification, partsFilter?: any, pagination?: any) {

    if (this.affectedPartIds.length > 0) {
      partsFilter = this.enrichPartsFilterByAffectedAssetIds(null);
      if (notification.type === NotificationType.INVESTIGATION) {
        this.partsFacade.setSupplierPartsAsBuiltSecond(pagination?.page || FIRST_PAGE, pagination?.pageSize || DEFAULT_PAGE_SIZE, this.tableAsBuiltSortList, toAssetFilter(partsFilter, true));
      } else {
        this.ownPartsFacade.setPartsAsBuiltSecond(pagination?.page || FIRST_PAGE, pagination?.pageSize || DEFAULT_PAGE_SIZE, this.tableAsBuiltSortList, toAssetFilter(partsFilter, true));
      }
    } else {
      this.partsFacade.setSupplierPartsAsBuiltSecondEmpty();
      this.ownPartsFacade.setPartsAsBuiltSecondEmpty();
    }

  }

  public ngOnDestroy(): void {
    this.notificationDetailFacade.selected = { data: null };
    this.notificationDetailFacade.unsubscribeSubscriptions();
    this.paramSubscription?.unsubscribe();
  }

  removeAffectedParts() {
    this.affectedPartIds = this.affectedPartIds.filter(value => {
      this.isSaveButtonDisabled = this.notificationFormGroup.invalid || this.affectedPartIds.length < 1;
      return !this.temporaryAffectedPartsForRemoval.some(part => part.id === value);
    });

    if (!this.affectedPartIds || this.affectedPartIds.length === 0) {
      if (this.selectedNotification.type === NotificationType.INVESTIGATION) {
        this.partsFacade.setSupplierPartsAsBuiltSecondEmpty();
        this.partsFacade.setSupplierPartsAsBuilt();
      } else {
        this.ownPartsFacade.setPartsAsBuiltSecondEmpty();
        this.ownPartsFacade.setPartsAsBuilt();
      }

      this.isSaveButtonDisabled = true;
    } else {
      this.isSaveButtonDisabled = this.notificationFormGroup.invalid || this.affectedPartIds.length < 1;
      this.deselectPartTrigger$.next(this.temporaryAffectedPartsForRemoval);
      this.currentSelectedAffectedParts$.next([]);
      this.temporaryAffectedPartsForRemoval = [];
      this.setAffectedPartsBasedOnNotificationType(this.selectedNotification);
      this.setAvailablePartsBasedOnNotificationType(this.selectedNotification);
    }

  }

  addAffectedParts() {
    this.temporaryAffectedParts.forEach(value => {
      if (!this.affectedPartIds.includes(value.id)) {
        this.affectedPartIds.push(value.id);
      }
    });
    this.isSaveButtonDisabled = this.notificationFormGroup.invalid || this.affectedPartIds.length < 1;
    this.deselectPartTrigger$.next(this.temporaryAffectedParts);
    this.currentSelectedAvailableParts$.next([]);
    this.temporaryAffectedParts = [];
    this.setAffectedPartsBasedOnNotificationType(this.selectedNotification);
    this.setAvailablePartsBasedOnNotificationType(this.selectedNotification);
  }


  public navigateBackToNotifications(): void {
    const { link } = getRoute(NOTIFICATION_BASE_ROUTE);
    this.router.navigate([ `/${ link }` ], {
      queryParams: {
        tabIndex: this.originTabIndex,
        pageNumber: this.originPageNumber,
      },
    });
  }

  private getNotificationByIdAndSelectNotificationAndLoadPartsBasedOnNotification(): void {
    const notificationId = this.route.snapshot.paramMap.get('notificationId');

    this.notificationsFacade
      .getNotificationById(notificationId)
      .subscribe({
        next: data => {
          this.selectNotificationAndLoadPartsBasedOnNotification(data);
        },
        error: () => {
        },
      });

  }


  private selectNotificationAndLoadPartsBasedOnNotification(notification: Notification) {
    console.log(notification, "selected from selectNotificationAndLoadPartsBasedOnNotification");
    this.selectedNotification = notification;

    this.affectedPartIds = notification.assetIds;
    this.tableType = notification.type === NotificationType.INVESTIGATION ? TableType.AS_BUILT_SUPPLIER : TableType.AS_BUILT_OWN;
    this.setAvailablePartsBasedOnNotificationType(this.selectedNotification);
    this.setAffectedPartsBasedOnNotificationType(this.selectedNotification);
    this.affectedPartsAsBuilt$ = notification.type === NotificationType.INVESTIGATION ? this.partsFacade.supplierPartsAsBuiltSecond$ : this.ownPartsFacade.partsAsBuiltSecond$;
    this.availablePartsAsBuilt$ = notification.type === NotificationType.INVESTIGATION ? this.partsFacade.supplierPartsAsBuilt$ : this.ownPartsFacade.partsAsBuilt$;
  }

  private updateSelectedNotificationState() {
    this.notificationDetailFacade.selected.data = {
      ...this.notificationDetailFacade.selected.data,
      ...this.notificationFormGroup.value,
      assetIds: this.affectedPartIds,
    };
  }

  private switchSelectedNotificationTypeAndResetParts() {
    this.selectedNotification.assetIds = [];
    this.affectedPartIds = [];
    // TODO: to switch notifications we need to build a proper request to make them empty
    //this.affectedPartsAsBuilt$ = this.partsFacade ...
    // TODO: comment back in if the upper todo was handled
    //this.availablePartsAsBuilt$ = this.selectedNotification.type === NotificationType.INVESTIGATION ? this.partsFacade.supplierPartsAsBuilt$ : this.ownPartsFacade.partsAsBuilt$;
    this.setAffectedPartsBasedOnNotificationType(this.selectedNotification);
    this.setAvailablePartsBasedOnNotificationType(this.selectedNotification);
  }

  protected readonly TableType = TableType;
  protected readonly MainAspectType = MainAspectType;
}
