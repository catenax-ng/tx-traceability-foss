import { PartSiteInformationAsPlanned } from '@page/parts/model/partSiteInformationAsPlanned.model';
import { TractionBatteryCode } from '@page/parts/model/tractionBatteryCode.model';

export interface DetailAspectModel {
  type: DetailSemanticDataModelType
  data: TractionBatteryCode | PartSiteInformationAsPlanned
}

export enum DetailSemanticDataModelType {
  TRACTIONBATTERYCODE = "TRACTIONBATTERYCODE",
  PARTSITEINFORMATIONASPLANNED = "PARTSITEINFORMATIONASPLANNED"
}

export enum DetailSemanticDataModelTypeCamelCase {
  TRACTIONBATTERYCODE = "TractionBatteryCode",
  PARTSITEINFORMATIONASPLANNED = "PartSiteInformationAsPlanned"
}
