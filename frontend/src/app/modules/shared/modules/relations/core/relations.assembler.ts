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

import { Part, QualityType } from '@page/parts/model/parts.model';
import { TreeElement, TreeStructure } from '@shared/modules/relations/model/relations.model';

export class RelationsAssembler {
  public static assemblePartForRelation(part: Part, idFallback?: string, fromParents?: boolean): TreeElement {
    // TODO: switch mapping children / parents
    const { id, name = idFallback, serialNumber, qualityType } = part || {};

    let children;
    if (fromParents === true && part.parents) {
      children = part.parents || {};
    } else {
      children = part.children;
    }
    console.dir(children);
    console.dir(part.parents);
    const mapQualityTypeToState = (type: QualityType) => (type === QualityType.Ok ? 'done' : type || 'error');
    const loadingOrErrorStatus = id ? 'loading' : 'error';
    const mappedOrFallbackStatus = mapQualityTypeToState(qualityType) || 'done';

    const state = !!children ? mappedOrFallbackStatus : loadingOrErrorStatus;
    return { id: id || idFallback, text: name, title: `${name || '--'} | ${serialNumber || id}`, state, children };
  }

  public static elementToTreeStructure(element: TreeElement): TreeStructure {
    if (!element) {
      return null;
    }

    const children: TreeStructure[] = element.children
      ? element.children.map(childId => ({
          id: childId,
          title: childId,
          state: 'loading',
          children: null,
        }))
      : null;

    return { ...element, state: element.state || 'done', children };
  }

  public static createLoadingElement(id: string): TreeElement {
    return { id, title: id, state: 'loading', children: null };
  }
}
