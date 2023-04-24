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
import { PartsState } from '@page/parts/core/parts.state';
import { Part, QualityType } from '@page/parts/model/parts.model';
import { View } from '@shared/model/view.model';
import { PartDetailsState } from '@shared/modules/part-details/core/partDetails.state';
import { LoadedElementsFacade } from '@shared/modules/relations/core/loaded-elements.facade';
import { RelationsAssembler } from '@shared/modules/relations/core/relations.assembler';
import { TreeDirection } from '@shared/modules/relations/model/relations.model';
import { PartsService } from '@shared/service/parts.service';
import { cloneDeep as _cloneDeep } from 'lodash-es';
import { Observable, of } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { SortDirection } from '../../../../../mocks/services/pagination.helper';

@Injectable()
export class PartDetailsFacade {
  constructor(
    private readonly partsService: PartsService,
    private readonly partsState: PartsState,
    private readonly partDetailsState: PartDetailsState,
    private readonly loadedElementsFacade: LoadedElementsFacade,
  ) {}

  public get selectedPart$(): Observable<View<Part>> {
    return this.partDetailsState.selectedPart$;
  }

  public set selectedPart(part: Part) {
    this.partDetailsState.selectedPart = { data: part };
  }

  public get selectedPart(): Part {
    return this.partDetailsState.selectedPart?.data;
  }

  public setPartFromTree(id: string): Observable<View<Part>> {
    return this.partsService.getPart(id).pipe(
      tap((part: Part) => {
        this.partDetailsState.selectedPart = { data: part };
      }),
      catchError(error => {
        this.partDetailsState.selectedPart = { error };
        return of(error);
      }),
    );
  }

  // TODO: still need quality type?
  public updateQualityType(qualityType: QualityType): Observable<Part> {
    const part = { ...this.selectedPart, qualityType };

    this.loadedElementsFacade.addLoadedElement(TreeDirection.RIGHT, RelationsAssembler.assemblePartForRelation(part));

    const { id } = part;
    const { data } = _cloneDeep(this.partsState.myParts);

    data.content = data.content.map(currentPart => (currentPart.id === id ? part : currentPart));
    this.partsState.myParts = { data };

    return this.partsService.patchPart(part);
  }

  public getChildPartDetails(part): Observable<Part[]> {
    return this.partsService.getPartDetailOfIds(part.children);
  }

  public sortChildParts(view: View<Part[]>, key: string, direction: SortDirection): Part[] {
    if (!view.data) return [];

    return this.partsService.sortParts(view.data, key, direction);
  }
}
