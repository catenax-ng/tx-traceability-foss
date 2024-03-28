/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { getI18nPageProvider } from '@core/i18n';
import { NotificationsRoutingModule } from '@page/notifications/notifications.routing';
import { NotificationDetailFacade } from '@page/notifications/core/notification-detail.facade';
import { NotificationDetailState } from '@page/notifications/core/notification-detail.state';
import { NotificationHelperService } from '@page/notifications/core/notification-helper.service';
import { NotificationsFacade } from '@page/notifications/core/notifications.facade';
import { NotificationsState } from '@page/notifications/core/notifications.state';
import { NotificationDetailComponent } from '@page/notifications/presentation/detail/notification-detail.component';
import { AcceptNotificationModalComponent } from '@page/notifications/presentation/modal/accept/accept-notification-modal.component';
import { AcknowledgeNotificationModalComponent } from '@page/notifications/presentation/modal/acknowledge/acknowledge-notification-modal.component';
import { ApproveNotificationModalComponent } from '@page/notifications/presentation/modal/approve/approve-notification-modal.component';
import { CancelNotificationModalComponent } from '@page/notifications/presentation/modal/cancel/cancel-notification-modal.component';
import { CloseNotificationModalComponent } from '@page/notifications/presentation/modal/close/close-notification-modal.component';
import { DeclineNotificationModalComponent } from '@page/notifications/presentation/modal/decline/decline-notification-modal.component';
import { NotificationTabComponent } from '@page/notifications/presentation/notification-tab/notification-tab.component';
import { NotificationComponent } from '@page/notifications/presentation/notification/notification.component';
import { PartsModule } from '@page/parts/parts.module';
import { NotificationCommonModalComponent } from '@shared/components/notification-common-modal/notification-common-modal.component';
import { ModalModule } from '@shared/modules/modal/modal.module';
import { FormatPartSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-part-semantic-data-model-to-camelcase.pipe';
import { FormatPaginationSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-pagination-semantic-data-model-to-camelcase.pipe';
import { FormatPartlistSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-partlist-semantic-data-model-to-camelcase.pipe';
import { SharedModule } from '@shared/shared.module';
import { TemplateModule } from '@shared/template.module';
import { NotificationsComponent } from './presentation/notifications.component';


@NgModule({
  declarations: [
    NotificationsComponent, NotificationDetailComponent,
    NotificationComponent,
    NotificationTabComponent,
    CloseNotificationModalComponent,
    ApproveNotificationModalComponent,
    CancelNotificationModalComponent,
    AcceptNotificationModalComponent,
    AcknowledgeNotificationModalComponent,
    DeclineNotificationModalComponent,
    NotificationCommonModalComponent,
  ],
  imports: [
    CommonModule,
    TemplateModule,
    SharedModule,
    NotificationsRoutingModule,
    PartsModule,
    ModalModule,
  ],
  providers: [
    NotificationsFacade,
    NotificationsState,
    NotificationDetailFacade,
    NotificationDetailState,
    NotificationHelperService,
    FormatPartSemanticDataModelToCamelCasePipe,
    FormatPaginationSemanticDataModelToCamelCasePipe,
    FormatPartlistSemanticDataModelToCamelCasePipe,
    NotificationCommonModalComponent,
    NotificationComponent,
    NotificationTabComponent,
    CloseNotificationModalComponent,
    ApproveNotificationModalComponent,
    CancelNotificationModalComponent,
    AcknowledgeNotificationModalComponent,
    AcceptNotificationModalComponent,
    DeclineNotificationModalComponent,
    ...getI18nPageProvider('page.alert'),
  ],
  exports: [
    NotificationTabComponent,
  ],
})
export class NotificationsModule {
}
