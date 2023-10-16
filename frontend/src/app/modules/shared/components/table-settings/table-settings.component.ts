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

import { Component, EventEmitter, Inject, Output } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TableSettingsService } from '@core/user/table-settings.service';
import { PartTableType } from '@shared/components/table/table.model';

@Component({
  selector: 'app-table-settings',
  templateUrl: 'table-settings.component.html',
  styleUrls: [ 'table-settings.component.scss' ],
})
export class TableSettingsComponent {
  /**
   * What does the Component do
   *
   * list all possible columns for a tabletype
   * mutate columnsettingsoption for visibilty of columns
   * change order of all possible columns
   * reset to default column view
   * apply changes to the table
   *  event when closed with boolean something changed
   *
   * What input does it need
   *
   * tabletype
   * a list of all default columns of the table (immutable default list with a specific order and content)
   * a list of all possible columns for a tabletype (mutable in order, add and delete columns)
   * mutate columnsettingsoptions (set false/true)
   *
   *
   *
   */
  @Output() changeSettingsEvent = new EventEmitter<void>();
  title: string;
  panelClass: string;

  tableType: PartTableType;
  defaultColumns: string[];
  defaultFilterColumns: string[]

  columnOptions: Map<string, boolean>;
  dialogColumns: string[];
  tableColumns: string[];
  filterColumns: string[];

  selectAllSelected: boolean;
  selectedColumn: string = null;


  constructor(public dialogRef: MatDialogRef<TableSettingsComponent>, @Inject(MAT_DIALOG_DATA) public data: any, public readonly tableSettingsService: TableSettingsService) {
    // Layout
    this.title = data.title;
    this.panelClass = data.panelClass;

    // Passed Data
    this.tableType = data.tableType;
    this.defaultColumns = data.defaultColumns;
    this.defaultFilterColumns = data.defaultFilterColumns;

    // Storage Data
    this.columnOptions = tableSettingsService.getColumnVisibilitySettings()[this.tableType].columnSettingsOptions;
    this.dialogColumns = tableSettingsService.getColumnVisibilitySettings()[this.tableType].columnsForDialog;
    this.tableColumns = tableSettingsService.getColumnVisibilitySettings()[this.tableType].columnsForTable;
    this.filterColumns = tableSettingsService.getColumnVisibilitySettings()[this.tableType].filterColumnsForTable;

    this.selectAllSelected = this.dialogColumns.length === this.tableColumns.length;

  }

  save() {
    // build new tableColumns how they should be displayed
    let newTableColumns: string[] = [];
    let newTableFilterColumns: string[] = [];
      // iterate over dialogColumns
      for(const column of this.dialogColumns) {
        // if item in dialogColumns is true in columnOptions --> add to new tableColumns
        if(this.columnOptions.get(column)) {
          newTableColumns.push(column);
          if(column === 'select' && this.tableType != PartTableType.AS_BUILT_CUSTOMER && this.tableType != PartTableType.AS_PLANNED_CUSTOMER) {
            newTableFilterColumns.push('Filter');
          } else {
            newTableFilterColumns.push('filter'+ column.charAt(0).toUpperCase() + column.slice(1))
          }
        }
      }

    // build visibilitySettings how they should be saved back
    // get Settingslist
    // set this tableType Settings from SettingsList to the new one
    let tableSettingsList = this.tableSettingsService.getColumnVisibilitySettings();
      let newTableSettings = {
        columnSettingsOptions: this.columnOptions,
        columnsForDialog: this.dialogColumns,
        columnsForTable: newTableColumns,
        filterColumnsForTable: newTableFilterColumns
      }
    tableSettingsList[this.tableType] = newTableSettings;
      console.log("saving as:", tableSettingsList);

    // save all values back to localstorage
    this.tableSettingsService.setColumnVisibilitySettings(this.tableType, tableSettingsList);

    // trigger action that table will refresh
    this.tableSettingsService.emitChangeEvent();
    this.dialogRef.close();
      // the tableconfig with the corresponding columns (and filter)
    // close the dialog

  }

  handleCheckBoxChange(item: string, isChecked: boolean) {
    this.columnOptions.set(item, isChecked);
    console.log(this.columnOptions);
  }

  handleListItemClick(event: MouseEvent, item: string) {
    let element = event.target as HTMLElement;

    if (element.tagName !== 'INPUT') {
      this.selectedColumn = item;
      element.classList.toggle('selected-item');
      console.log(this.selectedColumn);
    }
  }


  handleSortListItem(direction: string) {
    if(!this.selectedColumn) {
      console.log("no list item was selected")
      // TODO handle if no listitem was selected
      return;
    }

    let oldPosition = this.dialogColumns.indexOf(this.selectedColumn);
    let newPositon = direction === 'up' ? -1 : 1;

    if((oldPosition == 1 && direction === 'up') || (oldPosition === this.dialogColumns.length-1 && direction === 'down')) {
      return;
    }
      let temp = this.dialogColumns[oldPosition+newPositon];
    this.dialogColumns[oldPosition+newPositon] = this.selectedColumn;
    this.dialogColumns[oldPosition] = temp;
    console.log(this.dialogColumns);
  }


  selectAll(isChecked: boolean) {
    for(let column of this.dialogColumns) {
      if(column === 'select'){
        continue;
      }
      this.columnOptions.set(column,isChecked);
    }
    this.selectAllSelected = true;
    console.log(this.columnOptions);
  }

  resetColumns() {
    this.dialogColumns = [...this.defaultColumns];
    this.selectAll(true);
    console.log(this.defaultColumns);
    console.log(this.dialogColumns);
    console.log(this.columnOptions);

  }
}
