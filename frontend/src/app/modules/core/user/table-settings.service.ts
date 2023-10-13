import { Injectable } from '@angular/core';
import { PartTableType } from '@shared/components/table/table.model';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class TableSettingsService {
  private settingsKey = 'ViewTableSettings';
  private changeEvent = new Subject<void>();

  constructor() {}

  setColumnVisibilitySettings(partTableType: PartTableType, tableSettingsList: any ): void {
    // before setting anything, all maps in new tableSettingList should be stringified
    Object.keys(tableSettingsList).forEach(tableSetting => {
      const newMap = tableSettingsList[tableSetting].columnSettingsOptions;
      tableSettingsList[tableSetting].columnSettingsOptions = JSON.stringify(Array.from(newMap.entries()));
    })

    //

    //const newMap = tableSettingsList[partTableType].columnSettingsOptions;
    //tableSettingsList[partTableType].columnSettingsOptions = JSON.stringify(Array.from(newMap.entries()));
    localStorage.setItem(this.settingsKey, JSON.stringify(tableSettingsList));
  }

  // this returns whole settings whether empty / not for part / etc.
  getColumnVisibilitySettings(): any {
    const settingsJson = localStorage.getItem(this.settingsKey);
    let settingsObject = settingsJson ? JSON.parse(settingsJson) : null;
    if(settingsObject) {
      // iterate through all tabletypes and parse columnSettingsOption to a map
      Object.keys(settingsObject).forEach(tableSetting => {
        settingsObject[tableSetting].columnSettingsOptions = new Map(JSON.parse(settingsObject[tableSetting].columnSettingsOptions));

      })
      console.log("getColumnVisibilitySettings with map", settingsObject);
      return settingsObject;
    } else {
      console.log("getColumnVisibilitySettings no settings", settingsJson);
      return null;
    }

  }

  emitChangeEvent() {
    this.changeEvent.next();
  }

  getEvent() {
    return this.changeEvent.asObservable();
  }




}
