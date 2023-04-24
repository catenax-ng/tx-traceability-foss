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
import { Part } from '@page/parts/model/parts.model';
import { View } from '@shared/model/view.model';
import { RelationComponentState } from '@shared/modules/relations/core/component.state';
import { LoadedElementsFacade } from '@shared/modules/relations/core/loaded-elements.facade';
import { RelationsAssembler } from '@shared/modules/relations/core/relations.assembler';
import {
  OpenElements,
  TreeDirection,
  TreeElement,
  TreeStructure,
} from '@shared/modules/relations/model/relations.model';
import { PartsService } from '@shared/service/parts.service';
import _deepClone from 'lodash-es/cloneDeep';
import { Observable, of, Subject, Subscription } from 'rxjs';
import { bufferTime, catchError, debounceTime, filter, first, map, switchMap, tap } from 'rxjs/operators';

@Injectable()
export class RelationsFacade {
  private readonly requestPartDetailsQueue = new Subject<string[]>();
  private readonly requestPartDetailsStream = new Subject<TreeElement[]>();
  private requestPartDetailsQueueSubscription: Subscription;
  private requestPartDetailsQueueSubscriptionUpstream: Subscription;

  constructor(
    private readonly partsService: PartsService,
    private readonly loadedElementsFacade: LoadedElementsFacade,
    private readonly relationComponentState: RelationComponentState,
  ) {
    this.requestPartDetailsQueueSubscription = this.initRequestPartDetailQueue(TreeDirection.RIGHT).subscribe();
    this.requestPartDetailsQueueSubscriptionUpstream = this.initRequestPartDetailQueue(TreeDirection.LEFT).subscribe();
  }

  public get openElements$(): Observable<OpenElements> {
    return this.relationComponentState.openElements$.pipe(debounceTime(100));
  }

  public get openElementsUpstream$(): Observable<OpenElements> {
    return this.relationComponentState.openElementsUpstream$.pipe(debounceTime(100));
  }

  public get openElements(): OpenElements {
    return this.relationComponentState.openElements;
  }

  public get openElementsUpstream(): OpenElements {
    return this.relationComponentState.openElementsUpstream;
  }

  // This is used to add an element with its children to the opened list
  public openElementWithChildren(treeDirection: TreeDirection, { id, children }: TreeElement): void {
    const emptyChildren: OpenElements = {};
    const childElements =
      children?.reduce((p: OpenElements, c: string) => ({ ...p, [c]: null }), emptyChildren) || emptyChildren;

    if (treeDirection === TreeDirection.RIGHT) {
      this.relationComponentState.openElements = {
        ...this.relationComponentState.openElements,
        [id]: children,
        ...childElements,
      };

      this.loadChildrenInformation(treeDirection, children).subscribe();
    } else {
      this.relationComponentState.openElementsUpstream = {
        ...this.relationComponentState.openElementsUpstream,
        [id]: children,
        ...childElements,
      };

      this.loadChildrenInformation(treeDirection, children).subscribe();
    }
  }

  // This is only to update already opened elements.
  public updateOpenElement(treeDirection: TreeDirection, { id, children }: TreeElement): void {
    if (this.openElements[id] === undefined) {
      return;
    }

    if (treeDirection === TreeDirection.RIGHT) {
      this.relationComponentState.openElements = { ...this.relationComponentState.openElements, [id]: children };
    } else {
      this.relationComponentState.openElementsUpstream = { ...this.relationComponentState.openElementsUpstream, [id]: children };
    }

    this.loadChildrenInformation(treeDirection, children).subscribe();
  }

  public deleteOpenElement(treeDirection: TreeDirection, id: string): void {
    if (treeDirection === TreeDirection.RIGHT) {
      this.relationComponentState.openElements = this._deleteOpenElement(id, this.relationComponentState.openElements);
    } else {
      this.relationComponentState.openElementsUpstream = this._deleteOpenElement(
        id,
        this.relationComponentState.openElementsUpstream,
      );
    }
  }

  public formatOpenElementsToTreeData(treeDirection: TreeDirection, openElements: OpenElements): TreeStructure {
    const loadedData = treeDirection === TreeDirection.RIGHT ? this.loadedElementsFacade.loadedElements : this.loadedElementsFacade.loadedElementsUpstream;
    const keyList = Object.keys(openElements).reverse();
    const mappedData: Record<string, TreeStructure> = {};

    return keyList.reduce((p, key) => {
      const structure = RelationsAssembler.elementToTreeStructure(loadedData[key]);
      if (!structure) {
        return p;
      }

      structure.relations = structure.children?.length > 0 ? structure.children : null;
      structure.children = openElements[key]?.map(id => mappedData[id] || null).filter(child => !!child) || null;

      mappedData[key] = structure;
      return structure;
    }, {} as TreeStructure);
  }

  public isElementOpen(treeDirection: TreeDirection, id: string): boolean {
    const openElements = treeDirection === TreeDirection.RIGHT ? this.openElements : this.openElementsUpstream;
    const currentElement = openElements[id];
    if (!currentElement) {
      return false;
    }

    // Checks if the children, of the current element, are open
    return currentElement.some(childId => Object.keys(openElements).includes(childId));
  }

  public resetRelationState(): void {
    this.relationComponentState.resetOpenElements();

    // Not resetting already loaded data keep the requests to a minimum.
    // this.relationsState.resetLoadedElements();
  }

  public resetRelationStateUpstream(): void {
    this.relationComponentState.resetOpenElementsUpstream();

    // Not resetting already loaded data keep the requests to a minimum.
    // this.relationsState.resetLoadedElementsUpstream();
  }

  public openElementById(treeDirection: TreeDirection, elementId: string): void {
    const elementToOpen = treeDirection === TreeDirection.RIGHT ? this.loadedElementsFacade.loadedElements[elementId] : this.loadedElementsFacade.loadedElementsUpstream[elementId];
    this.openElementWithChildren(treeDirection, elementToOpen);
  }

  public closeElementById(treeDirection: TreeDirection, elementId: string): void {
    const elementToClose = treeDirection === TreeDirection.RIGHT ?  this.loadedElementsFacade.loadedElements[elementId] : this.loadedElementsFacade.loadedElementsUpstream[elementId];
    elementToClose.children.forEach(childId => this.deleteOpenElement(treeDirection, childId));
  }

  public getRootPart(id: string): Observable<View<Part>> {
    return this.partsService.getPart(id).pipe(
      map((part: Part) => ({ data: part })),
      catchError((error: Error) => of({ error })),
    );
  }

  private _deleteOpenElement(id: string, openElements: OpenElements): OpenElements {
    let clonedElements = _deepClone(openElements);
    if (clonedElements[id]?.length) {
      clonedElements[id].forEach(childId => (clonedElements = this._deleteOpenElement(childId, clonedElements)));
    }

    delete clonedElements[id];
    return clonedElements;
  }

  private loadChildrenInformation(treeDirection: TreeDirection, children: string[]): Observable<TreeElement[]> {
    if (!children) {
      return of(null).pipe(first());
    }

    const loadedElements = treeDirection === TreeDirection.RIGHT ? this.loadedElementsFacade.loadedElements : this.loadedElementsFacade.loadedElementsUpstream;
    const getLoadedElement = (id: string) => loadedElements[id];
    const isNoChildLoading = children.every(id => getLoadedElement(id) && getLoadedElement(id)?.state != 'loading');

    if (isNoChildLoading) {
      const mappedChildren = children.map(childId => loadedElements[childId]);
      this.addLoadedElements(treeDirection, mappedChildren);
      return of(mappedChildren).pipe(first());
    }

    this.requestPartDetailsQueue.next(children);
    return this.requestPartDetailsStream.asObservable();
  }

  private initRequestPartDetailQueue(treeDirection: TreeDirection): Observable<TreeElement[]> {
    let children;
    return this.requestPartDetailsQueue.pipe(
      bufferTime(500),
      filter(childList => !!childList.length),
      switchMap(childList => {
        children = childList.reduce((p, c) => [...p, ...c], []);
        return this.partsService.getPartDetailOfIds(children);
      }),
      catchError(_ => of(children.map(id => ({ id, children: [] } as Part)))),
      map(childrenData =>
        children.map(id => childrenData.find(data => data.id === id) || ({ id, children: [] } as Part)),
      ),
      map(childrenData => childrenData.map(child => RelationsAssembler.assemblePartForRelation(child))),
      tap(childrenData => this.addLoadedElements(treeDirection, childrenData)),
      tap(childrenData => this.requestPartDetailsStream.next(childrenData)),
    );
  }

  private addLoadedElements(treeDirection: TreeDirection, elements: TreeElement[]): void {
    elements.forEach(element => {
      this.loadedElementsFacade.addLoadedElement(treeDirection, element);
      this.updateOpenElement(treeDirection, element);
    });
  }
}
