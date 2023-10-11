import { Injectable } from '@angular/core';
import { PartTableType } from '@shared/components/table/table.model';

@Injectable({
  providedIn: 'root',
})
export class TableSettingsService {
  private settingsKey = 'userTableSettings'

  constructor() {}

  setColumnVisibilitySettings(partTableType: PartTableType, displayedColumns: string[] ): void {
    console.log(partTableType.toString());
    localStorage.setItem(partTableType.toString(), JSON.stringify(displayedColumns));
  }

  getColumnVisibilitySettings(partTableType: PartTableType): any {
    const tableSettings = localStorage.getItem(partTableType.toString());
    if (tableSettings) {
      return JSON.parse(tableSettings);
    } else {
      return [];
    }
  }
}
