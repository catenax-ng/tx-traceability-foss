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

import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { RelationComponentState } from '@shared/modules/relations/core/component.state';
import { LoadedElementsFacade } from '@shared/modules/relations/core/loaded-elements.facade';
import { LoadedElementsState } from '@shared/modules/relations/core/loaded-elements.state';
import { RelationsFacade } from '@shared/modules/relations/core/relations.facade';
import { TreeDirection, TreeElement, TreeStructure } from '@shared/modules/relations/model/relations.model';
import { PartsService } from '@shared/service/parts.service';
import { waitFor } from '@testing-library/angular';
import { firstValueFrom, of } from 'rxjs';
import { debounceTime, map } from 'rxjs/operators';
import {
  MOCK_part_1,
  MOCK_part_2,
  MOCK_part_3,
  mockAssetList,
} from '../../../../../mocks/services/parts-mock/parts.test.model';

describe('Relations facade', () => {
  const childDescriptionsToChild = children => children.map(({ id }) => id);
  let relationsFacade: RelationsFacade,
    loadedElementsFacade: LoadedElementsFacade,
    componentStateMock: RelationComponentState;

  beforeEach(() => {
    const partsServiceMok = {
      getPart: id => of(mockAssetList[id]).pipe(map(part => PartsAssembler.assemblePart(part))),
      getPartDetailOfIds: assetIds =>
        of(assetIds.map(id => mockAssetList[id])).pipe(map(parts => PartsAssembler.assemblePartList(parts))),
    } as PartsService;

    loadedElementsFacade = new LoadedElementsFacade(new LoadedElementsState());
    componentStateMock = new RelationComponentState();
    relationsFacade = new RelationsFacade(partsServiceMok, loadedElementsFacade, componentStateMock);
  });

  const getOpenElements = async () => await firstValueFrom(componentStateMock.openElements$.pipe(debounceTime(700)));
  describe('openElementWithChildren', () => {
    it('should set open elements state to new one', async () => {
      const { id, childDescriptions } = MOCK_part_1;
      const mockTreeElement = { id, children: childDescriptionsToChild(childDescriptions) } as TreeElement;
      const expected = {
        [MOCK_part_1.id]: childDescriptionsToChild(MOCK_part_1.childDescriptions),
        [MOCK_part_2.id]: childDescriptionsToChild(MOCK_part_2.childDescriptions),
        [MOCK_part_3.id]: childDescriptionsToChild(MOCK_part_3.childDescriptions),
      };

      relationsFacade.openElementWithChildren(TreeDirection.RIGHT, mockTreeElement);
      expect(await getOpenElements()).toEqual(expected);
    });
  });

  describe('updateOpenElement', () => {
    it('should not update open elements if element is not already open', async () => {
      const { id, childDescriptions } = MOCK_part_1;
      const mockTreeElement = { id, children: childDescriptionsToChild(childDescriptions) } as TreeElement;
      const expected = {};

      relationsFacade.updateOpenElement(TreeDirection.RIGHT, mockTreeElement);
      expect(await getOpenElements()).toEqual(expected);
    });
  });

  describe('deleteOpenElement', () => {
    it('should cancel opened element', async () => {
      const { id, childDescriptions } = MOCK_part_1;
      const children = childDescriptionsToChild(childDescriptions);
      const mockTreeElement = { id, children } as TreeElement;
      const expected = { MOCK_part_1: ['MOCK_part_2', 'MOCK_part_3'], MOCK_part_3: ['MOCK_part_5'] };

      relationsFacade.openElementWithChildren(TreeDirection.RIGHT, mockTreeElement);
      relationsFacade.deleteOpenElement(TreeDirection.RIGHT, children[0]);

      expect(await getOpenElements()).toEqual(expected);
    });

    it('should cancel open element', async () => {
      const { id, childDescriptions } = MOCK_part_1;
      const mockTreeElement = { id, children: childDescriptionsToChild(childDescriptions) } as TreeElement;
      const expected_all = {
        [MOCK_part_1.id]: childDescriptionsToChild(MOCK_part_1.childDescriptions),
        [MOCK_part_2.id]: childDescriptionsToChild(MOCK_part_2.childDescriptions),
        [MOCK_part_3.id]: childDescriptionsToChild(MOCK_part_3.childDescriptions),
      };

      relationsFacade.openElementWithChildren(TreeDirection.RIGHT, mockTreeElement);
      const allOpenElements = await getOpenElements();
      await waitFor(() => expect(allOpenElements).toEqual(expected_all));

      relationsFacade.deleteOpenElement(TreeDirection.RIGHT, MOCK_part_2.id);

      const expected_deleted = {
        [MOCK_part_1.id]: childDescriptionsToChild(MOCK_part_1.childDescriptions),
        [MOCK_part_3.id]: childDescriptionsToChild(MOCK_part_3.childDescriptions),
      };

      const deletedOpenElements = await getOpenElements();
      await waitFor(() => expect(deletedOpenElements).toEqual(expected_deleted));
    });
  });

  describe('formatOpenElementsToTreeData', () => {
    it('should format data', async () => {
      const expected = {
        id: 'MOCK_part_1',
        state: 'done',
        children: [
          {
            children: [],
            id: 'MOCK_part_2',
            relations: [
              {
                children: null,
                id: 'MOCK_part_4',
                state: 'loading',
                title: 'MOCK_part_4',
              },
            ],
            state: 'Minor',
            text: 'BMW 520d Touring',
            title: 'BMW 520d Touring | 3N1CE2CPXFL392065',
          },
          {
            children: [],
            id: 'MOCK_part_3',
            relations: [
              {
                children: null,
                id: 'MOCK_part_5',
                state: 'loading',
                title: 'MOCK_part_5',
              },
            ],
            state: 'Major',
            text: 'A 180 Limousine',
            title: 'A 180 Limousine | JF1ZNAA12E8706066',
          },
        ],
        relations: [
          {
            children: null,
            id: 'MOCK_part_2',
            state: 'loading',
            title: 'MOCK_part_2',
          },
          {
            children: null,
            id: 'MOCK_part_3',
            state: 'loading',
            title: 'MOCK_part_3',
          },
        ],
      } as TreeStructure;

      const { id, childDescriptions } = MOCK_part_1;
      const mockTreeElement = { id, children: childDescriptionsToChild(childDescriptions) } as TreeElement;

      loadedElementsFacade.addLoadedElement(mockTreeElement);
      relationsFacade.openElementWithChildren(TreeDirection.RIGHT, mockTreeElement);
      expect(relationsFacade.formatOpenElementsToTreeData(TreeDirection.RIGHT, await getOpenElements())).toEqual(
        expected,
      );
    });
  });
});
