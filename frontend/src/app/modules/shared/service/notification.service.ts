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

import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiService } from '@core/api/api.service';
import { environment } from '@env';
import { NotificationAssembler } from '@shared/assembler/notification.assembler';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { NotificationChannel } from '@shared/components/multi-select-autocomplete/table-type.model';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { provideFilterListForNotifications } from '@shared/helper/filter-helper';
import { Severity } from '@shared/model/severity.model';
import type { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import {
  Notification,
  NotificationCreateResponse,
  NotificationDeeplinkFilter,
  NotificationResponse,
  Notifications,
  NotificationsResponse,
  NotificationStatus,
} from '../model/notification.model';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private readonly url = environment.apiUrl;

  constructor(private readonly apiService: ApiService) {
  }

  public getNotifications(page: number, pageSize: number, sorting: TableHeaderSort[], channel: NotificationChannel, filter?: NotificationDeeplinkFilter, fullFilter?: any): Observable<Notifications> {
    const sort = sorting.length ? sorting.map(array => `${ array[0] },${ array[1] }`) : [ 'createdDate,desc' ];
    const requestUrl = this.notificationUrl() + '/filter';
    const channelFilter = channel === NotificationChannel.RECEIVER ? 'channel,EQUAL,RECEIVER,AND' : 'channel,EQUAL,SENDER,AND';
    const additionalFilters = new Set([ ...provideFilterListForNotifications(filter, fullFilter), channelFilter ]);

    const body = {
      pageAble: {
        page: page,
        size: pageSize,
        sort: sort,
      },
      searchCriteria: {
        filter: [ ...additionalFilters ],
      },
    };

    return this.apiService
      .post<NotificationsResponse>(requestUrl, body)
      .pipe(map(data => NotificationAssembler.assembleNotifications(data)));
  }


  public getNotificationById(id: string): Observable<Notification> {
    const requestUrl = this.notificationUrl();
    return this.apiService
      .get<NotificationResponse>(`${ requestUrl }/${ id }`)
      .pipe(map(notification => NotificationAssembler.assembleNotification(notification)));
  }

  public createNotification(partIds: string[], description: string, severity: Severity, bpn: string, isAsBuilt: boolean, type: string, title: string): Observable<string> {
    const body = { partIds, description, severity, receiverBpn: bpn, isAsBuilt, type, title };

    return this.apiService.post<NotificationCreateResponse>(`${ this.url }/notifications`, body).pipe(map(({ id }) => id));
  }


  public closeNotification(id: string, reason: string): Observable<void> {
    const requestUrl = this.notificationUrl();
    const body = { reason };
    return this.apiService.post<void>(`${ requestUrl }/${ id }/close`, body);
  }

  public approveNotification(id: string): Observable<void> {
    const requestUrl = this.notificationUrl();
    return this.apiService.post<void>(`${ requestUrl }/${ id }/approve`);
  }

  public cancelNotification(id: string): Observable<void> {
    const requestUrl = this.notificationUrl();
    return this.apiService.post<void>(`${ requestUrl }/${ id }/cancel`);
  }

  public updateNotification(
    id: string,
    status: NotificationStatus.ACKNOWLEDGED | NotificationStatus.ACCEPTED | NotificationStatus.DECLINED,
    reason = '',
  ): Observable<void> {
    const requestUrl = this.notificationUrl();
    const body = { reason, status };
    return this.apiService.post<void>(`${ requestUrl }/${ id }/update`, body);
  }

  public getDistinctFilterValues(channel: NotificationChannel, fieldNames: string, startsWith: string) {
    const mappedFieldName = PartsAssembler.mapFieldNameToApi(fieldNames);
    const requestUrl = this.notificationUrl();
    let params = new HttpParams()
      .set('fieldName', mappedFieldName)
      .set('startWith', startsWith)
      .set('size', 200)
      .set('channel', channel);

    return this.apiService
      .getBy<any>(`${ requestUrl }/distinctFilterValues`, params);

  }

  public notificationUrl(): string {
    return this.url + '/notifications';
  }
}
