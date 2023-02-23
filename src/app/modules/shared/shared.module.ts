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

import { NgModule } from '@angular/core';
import { MatLegacyPaginatorIntl as MatPaginatorIntl } from '@angular/material/legacy-paginator';
import { RouterModule } from '@angular/router';
import { NotificationOverviewComponent } from '@shared/components/notification-overview/notification-overview.component';
import { NotificationReasonComponent } from '@shared/components/notification-reason/notification-reason.component';
import { NotificationModalContentComponent } from '@shared/modules/notification/modal/content/notification-modal-content.component';
import { I18NextModule } from 'angular-i18next';
import { AvatarComponent } from './components/avatar/avatar.component';
import { BreadcrumbsComponent } from './components/breadcrumbs/breadcrumbs.component';
import { ButtonComponent } from './components/button/button.component';
import { CtaSnackbarComponent } from './components/call-to-action-snackbar/cta-snackbar.component';
import { CardIconComponent } from './components/card-icon/card-icon.component';
import { CardListComponent } from './components/card-list/card-list.component';
import { ToKeyValuePipe } from './components/card-list/card-list.pipe';
import { DataLoadingErrorComponent } from './components/data-loading-error/data-loading-error.component';
import { HeaderComponent } from './components/header/header.component';
import { LanguageSelectorComponent } from './components/language-selector/language-selector.component';
import { PaginatorIntlService } from './components/pagination/paginator-intl.service';
import { QualityTypeComponent } from './components/quality-type/quality-type.component';
import { RequestInvestigationComponent } from './components/request-investigation/request-investigation.component';
import { ScrollWithShadowComponent } from './components/scroll-with-shadow/scroll-with-shadow.component';
import { SelectComponent } from './components/select/select.component';
import { ValueToLablePipe } from './components/select/valueToLable.pipe';
import { SidenavWrapperComponent } from './components/sidenav/sidenav-wrapper.component';
import { TableComponent } from './components/table/table.component';
import { TextareaComponent } from './components/textarea/textarea.component';
import { ToastContainerComponent } from './components/toasts/toast-container/toast-container.component';
import { ToastMessageComponent } from './components/toasts/toast-message/toast-message.component';
import { RoleDirective } from './directives/role.directive';
import { TabAsPanelDirective } from './directives/tabs/tab-as-panel.directive';
import { TooltipDirective } from './directives/tooltip.directive';
import { ViewContainerDirective } from './directives/view-container.directive';
import { AutoFormatPipe } from './pipes/auto-format.pipe';
import { ErrorMessagePipe } from './pipes/error-message.pipe';
import { FormatDatePipe } from './pipes/format-date.pipe';
import { I18nPipe } from './pipes/i18n.pipe';
import { PartsService } from './service/parts.service';
import { StaticIdService } from './service/staticId.service';
import { TemplateModule } from './template.module';

@NgModule({
  declarations: [
    ToastContainerComponent,
    ToastMessageComponent,
    BreadcrumbsComponent,
    HeaderComponent,
    ButtonComponent,
    TableComponent,
    TooltipDirective,
    RoleDirective,
    I18nPipe,
    AutoFormatPipe,
    FormatDatePipe,
    ViewContainerDirective,
    AvatarComponent,
    LanguageSelectorComponent,
    CardIconComponent,
    CardListComponent,
    ToKeyValuePipe,
    SelectComponent,
    DataLoadingErrorComponent,
    TabAsPanelDirective,
    SidenavWrapperComponent,
    TextareaComponent,
    ErrorMessagePipe,
    RequestInvestigationComponent,
    CtaSnackbarComponent,
    ScrollWithShadowComponent,
    QualityTypeComponent,
    ValueToLablePipe,
    NotificationOverviewComponent,
    NotificationReasonComponent,
    NotificationModalContentComponent,
  ],
  imports: [TemplateModule, RouterModule, I18NextModule],
  exports: [
    ToastContainerComponent,
    ToastMessageComponent,
    BreadcrumbsComponent,
    HeaderComponent,
    ButtonComponent,
    TableComponent,
    TooltipDirective,
    RoleDirective,
    I18nPipe,
    FormatDatePipe,
    AutoFormatPipe,
    ViewContainerDirective,
    AvatarComponent,
    I18NextModule,
    LanguageSelectorComponent,
    CardIconComponent,
    CardListComponent,
    ToKeyValuePipe,
    SelectComponent,
    DataLoadingErrorComponent,
    TabAsPanelDirective,
    SidenavWrapperComponent,
    TextareaComponent,
    ErrorMessagePipe,
    RequestInvestigationComponent,
    QualityTypeComponent,
    NotificationOverviewComponent,
    NotificationReasonComponent,
    NotificationModalContentComponent,
  ],
  providers: [
    FormatDatePipe,
    StaticIdService,
    PartsService,
    ErrorMessagePipe,
    {
      provide: MatPaginatorIntl,
      useClass: PaginatorIntlService,
    },
  ],
})
export class SharedModule {}
