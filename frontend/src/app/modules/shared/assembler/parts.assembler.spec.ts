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

import { CalendarDateModel } from '@core/model/calendar-date.model';
import { Pagination } from '@core/model/pagination.model';
import { Part, QualityType, SemanticDataModel } from '@page/parts/model/parts.model';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { of } from 'rxjs';

describe('PartsAssembler', () => {
  const page = <T>(content: T[]): Pagination<T> => ({
    content,
    pageCount: 1,
    totalItems: content.length,
    page: 0,
    pageSize: content.length,
  });

  describe('assembleParts', () => {
    it('should return null if array is empty or undefined', () => {
      const emptyPage = { content: [], page: 0, pageCount: 0, pageSize: 0, totalItems: 0 };
      expect(PartsAssembler.assembleParts(null)).toEqual(emptyPage);
      expect(PartsAssembler.assembleParts(page([]))).toEqual(emptyPage);
    });

    it('should format the object correctly', () => {
      const testData = [];
      const expected = [];
      for (let i = 0; i < 3; i++) {
        const id = 'id_' + i;
        const idShort = 'idShort_' + i;
        const semanticModelId = 'semanticModelId';
        const manufacturerId = 'manufacturerId';
        const manufacturerName = 'manufacturerName';
        const semanticModel = {
          manufacturingDate: 'manufacturingDate',
          manufacturingCountry: 'manufacturingCountry',
          manufacturerPartId: 'manufacturerPartId',
          customerPartId: 'customerPartId',
          nameAtManufacturer: 'nameAtManufacturer',
          nameAtCustomer: 'nameAtCustomer'
        }
        const owner = 'OWN';
        const activeAlert = false;
        const underInvestigation = false;
        const childRelations = [{ id: 'id', idShort: 'idShort' }];
        const parentRelations = [];
        const qualityType = 'Ok';
        const van = 'van';
        const semanticDataModel = 'BATCH';

        testData.push({
          id,
          idShort,
          semanticModelId,
          manufacturerId,
          manufacturerName,
          semanticModel,
          owner,
          activeAlert,
          underInvestigation,
          childRelations,
          parentRelations,
          qualityType,
          van,
          semanticDataModel
        });

        expected.push({
          id,
          name: semanticModel.nameAtManufacturer,
          manufacturer: manufacturerName,
          semanticModelId: semanticModelId,
          partNumber: semanticModel.manufacturerPartId,
          productionCountry: semanticModel.manufacturingCountry,
          nameAtCustomer: semanticModel.nameAtCustomer,
          customerPartId: semanticModel.customerPartId,
          qualityType: QualityType.Ok,
          productionDate: new CalendarDateModel(semanticModel.manufacturingDate),
          children: childRelations.map(child => child.id),
          parents: [],
          activeInvestigation: false,
          activeAlert: false,
          van: 'van',
          semanticDataModel: SemanticDataModel.BATCH
        });
      }

      expect(JSON.stringify(PartsAssembler.assembleParts(page(testData)).content)).toEqual(JSON.stringify(expected));
    });
  });

  describe('filterPartForView', () => {
    const productionDate = 'productionDate';
    const qualityType = 'qualityType';
    const semanticModelId = 'semanticModelId';
    const semanticDataModel = 'semanticDataModel';
    const manufacturingDate = 'manufacturingDate'
 // TODO
    it('should clean up data for part view', () => {
      const data = { semanticDataModel, semanticModelId, manufacturingDate, qualityType, test: '' } as unknown as Part;
      expect(PartsAssembler.filterPartForView({ data })).toEqual({
        data: { name: undefined, productionDate, semanticModelId, semanticDataModel } as unknown as Part,
      });
    });

    it('should return view if data is not set', () => {
      const viewData = {};
      expect(PartsAssembler.filterPartForView(viewData)).toEqual(viewData);
      expect(PartsAssembler.filterPartForView(undefined)).toEqual(undefined);
    });
  });

  describe('mapPartForManufacturerView', () => {
    const manufacturer = 'manufacturer';
    const partNumber = 'partNumber';
    const name = 'name';
    const semanticModelId = 'semanticModelId';
    const van = 'van';

    it('should clean up data for manufacturer view', done => {
      const data = { manufacturer, partNumber, name, semanticModelId, test: '', van } as unknown as Part;
      of({ data })
        .pipe(PartsAssembler.mapPartForManufacturerView())
        .subscribe(result => {
          expect(result).toEqual({
            data: { manufacturer, partNumber, semanticModelId, van } as unknown as Part,
          });
          done();
        });
    });

    it('should return view if data is not set', done => {
      const viewData = {};
      of(viewData)
        .pipe(PartsAssembler.mapPartForManufacturerView())
        .subscribe(result => {
          expect(result).toEqual(viewData);
          done();
        });
    });
  });

  describe('mapPartForCustomerView', () => {
    const customerPartId = 'customerPartId';
    const nameAtCustomer = 'nameAtCustomer';

    it('should clean up data for customer view', done => {
      const data = { customerPartId, nameAtCustomer, test: '' } as unknown as Part;
      of({ data })
        .pipe(PartsAssembler.mapPartForCustomerOrPartSiteView())
        .subscribe(result => {
          expect(result).toEqual({ data: { customerPartId, nameAtCustomer } as unknown as Part });
          done();
        });
    });

    it('should return view if data is not set', done => {
      const viewData = {};
      of(viewData)
        .pipe(PartsAssembler.mapPartForCustomerOrPartSiteView())
        .subscribe(result => {
          expect(result).toEqual(viewData);
          done();
        });
    });
  });
});
