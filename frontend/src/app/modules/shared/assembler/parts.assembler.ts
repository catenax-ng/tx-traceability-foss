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

import { Pagination, PaginationResponse } from '@core/model/pagination.model';
import { PaginationAssembler } from '@core/pagination/pagination.assembler';
import {AsBuiltAspectModel, AsPlannedAspectModel, SemanticModel} from '@page/parts/model/aspectModels.model';
import { Part, PartResponse, QualityType } from '@page/parts/model/parts.model';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { View } from '@shared/model/view.model';
import { OperatorFunction } from 'rxjs';
import { map } from 'rxjs/operators';

export class PartsAssembler {

  public static createSemanticModelFromPartResponse(partResponse: PartResponse): SemanticModel {
    let proplist= {};
    partResponse.detailAspectModels.map((detailAspectModel) => {
      proplist = {...proplist, ...detailAspectModel.data};   //detailSemanticModel.data;
    })

    return proplist;
  }

  public static assemblePart(partResponse: PartResponse): Part {
    if (!partResponse) {
      return null;
    }

    let createdSemanticModel = PartsAssembler.createSemanticModelFromPartResponse(partResponse);

    // Access the partId property

    const partId = (partResponse.detailAspectModels[0].data as AsBuiltAspectModel)?.partId;
    const customerPartId = (partResponse.detailAspectModels[0].data as AsBuiltAspectModel)?.customerPartId;
    const nameAtCustomer = (partResponse.detailAspectModels[0].data as AsBuiltAspectModel)?.nameAtCustomer;
    const manufacturingDate = (partResponse.detailAspectModels[0].data as AsBuiltAspectModel)?.manufacturingDate;
    const manufacturingCountry = (partResponse.detailAspectModels[0].data as AsBuiltAspectModel)?.manufacturingCountry;
    const validityPeriodFrom = (partResponse.detailAspectModels[0].data as AsPlannedAspectModel)?.validityPeriodFrom;
    const validityPeriodTo = (partResponse.detailAspectModels[0].data as AsPlannedAspectModel)?.validityPeriodTo;

    console.log(partId); // Outputs the partId value from the AsBuiltAspectModel

    return {
      id: partResponse.id,
      semanticModelId: partResponse.semanticModelId,
      manufacturer: partResponse.businessPartner,
      name: partResponse.idShort,
      children: partResponse.childRelations.map(child => child.id) || [],
      parents: partResponse.parentRelations?.map(parent => parent.id) || [],
      activeAlert: partResponse.activeAlert || false,
      activeInvestigation: partResponse.underInvestigation || false,
      qualityType: partResponse.qualityType || QualityType.Ok,
      van: partResponse.van || '--',
      semanticDataModel: partResponse.semanticDataModel,
      classification: partResponse.classification,
      semanticModel: createdSemanticModel,
      // as built
      partId: partId,
      customerPartId: customerPartId,
      nameAtCustomer: nameAtCustomer,
      manufacturingDate: manufacturingDate,
      manufacturingCountry: manufacturingCountry,

      // as planned
      validityPeriodFrom: validityPeriodFrom,
      validityPeriodTo: validityPeriodTo

    };
  }
/*
    return {
      id: partResponse.id,
      name: partResponse.semanticModel.nameAtManufacturer,
      manufacturer: partResponse.manufacturerName,
      semanticModelId: partResponse.semanticModelId,
      partNumber: partResponse.semanticModel.manufacturerPartId,
      productionCountry: partResponse.semanticModel.manufacturingCountry,
      nameAtCustomer: partResponse.semanticModel.nameAtCustomer,
      customerPartId: partResponse.semanticModel.customerPartId,
      qualityType: partResponse.qualityType || QualityType.Ok,
      productionDate: new CalendarDateModel(partResponse.semanticModel.manufacturingDate),
      children: partResponse.childRelations.map(child => child.id) || [],
      parents: partResponse.parentRelations?.map(parent => parent.id) || [],
      activeInvestigation: partResponse.underInvestigation || false,
      activeAlert: partResponse.activeAlert || false,
      van: partResponse.van || '--',
      semanticDataModel: partResponse.semanticDataModel
    };
  }
*/
  /* OLD RESPONSE
export interface PartResponse {
  id: string;
  idShort: string;
  semanticModelId: string;
  manufacturerId: string;
  manufacturerName: string;
  semanticModel: SemanticModel;
  owner: Owner;
  childRelations: Array<{ id: string; idShort: string }>;
  parentRelations?: Array<{ id: string; idShort: string }>;
  activeAlert: boolean;
  underInvestigation?: boolean;
  qualityType: QualityType;
  van?: string;
  semanticDataModel: SemanticDataModel;
}
 */
/*
  export interface PartResponse {
  id: string;
  owner: Owner;
  activeAlert: boolean;
  qualityType: QualityType;
  underInvestigation: boolean;
  semanticDataModelType: SemanticDataModel;
  childRelations: Array<Relation>;
  parentRelations: Array<Relation>;
  idShort: string;
  van: string;
  businessPartner: string;
  nameAtManufacturer: string;
  classification: string;
  detailSemanticModels: Array<DetailAspectModel>

}
*/

  public static assembleOtherPart(partResponse: PartResponse): Part {
    if (!partResponse) {
      return null;
    }

    return { ...PartsAssembler.assemblePart(partResponse), qualityType: partResponse.qualityType };
  }

  public static assembleParts(parts: PaginationResponse<PartResponse>): Pagination<Part> {
    return PaginationAssembler.assemblePagination(PartsAssembler.assemblePart, parts);
  }

  public static assemblePartList(parts: PartResponse[]): Part[] {
    const partCopy = [...parts];
    return partCopy.map(part => PartsAssembler.assemblePart(part));
  }

  public static assembleOtherParts(parts: PaginationResponse<PartResponse>): Pagination<Part> {
    return PaginationAssembler.assemblePagination(PartsAssembler.assembleOtherPart, parts);
  }

  public static filterPartForView(viewData: View<Part>): View<Part> {
    if (!viewData?.data) {
      return viewData;
    }

    const {
      name,
      semanticDataModel,
      semanticModelId,
      manufacturingDate,
      manufacturingCountry,
      classification ,

    } = viewData.data;
    return { data: {
        name,
        semanticDataModel,
        semanticModelId,
        manufacturingDate,
        manufacturingCountry,
        classification ,


      } as Part };
  }

  public static mapPartForView(): OperatorFunction<View<Part>, View<Part>> {
    return map(PartsAssembler.filterPartForView);
  }

  public static mapPartForManufacturerView(): OperatorFunction<View<Part>, View<Part>> {
    return map(viewData => {
      if (!viewData.data) {
        return viewData;
      }

      const {
        manufacturer,
        partId,
        //nameAtManuFacturer?
        van,

      } = viewData.data;
      return { data: { manufacturer, partId, van } as Part };
    });
  }

  public static mapPartForCustomerView(): OperatorFunction<View<Part>, View<Part>> {
    return map(viewData => {
      if (!viewData.data) {
        return viewData;
      }

      const { nameAtCustomer, customerPartId } = viewData.data;
      return { data: { nameAtCustomer, customerPartId } as Part };
    });
  }

  public static mapSortToApiSort(sorting: TableHeaderSort): string {
    if (!sorting) {
      return '';
    }



    const localToApiMapping = new Map<string, string>([
      ['id', 'id'],
      ['semanticDataModel', 'semanticDataModel'],
      ['name', 'nameAtManufacturer'],
      ['manufacturer', 'manufacturerName'],
      ['semanticModelId', 'manufacturerPartId'],
      ['partNumber', 'customerPartId'],
      ['productionCountry', 'manufacturingCountry'],
      ['nameAtCustomer', 'nameAtCustomer'],
      ['customerPartId', 'customerPartId'],
      ['qualityType', 'qualityType'],
      ['productionDate', 'manufacturingDate'],
    ]);

    return `${localToApiMapping.get(sorting[0]) || sorting},${sorting[1]}`;
  }
}
