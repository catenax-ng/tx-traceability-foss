import { Component, ElementRef, Inject, ViewChildren } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TableSettingsService } from '@core/user/table-settings.service';
import { PartTableType } from '@shared/components/table/table.model';

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
@Component({
  selector: 'app-table-settings',
  templateUrl: 'table-settings.component.html',
  styleUrls: ['table-settings.component.scss']
})
export class TableSettingsComponent {
  @ViewChildren('checkbox') checkbox: ElementRef;
  title: string;
  tableDisplayedColumnsRef: string[];
  tableDisplayedFilterColumnsRef: string[];

  tableType: PartTableType;

  columnSelection: Map<string,boolean> = new Map<string, boolean>();
  defaultColumns: string[];
  selectAllSelected: boolean;

  selectedColumn: string = null;




  constructor(public dialogRef: MatDialogRef<TableSettingsComponent>, @Inject(MAT_DIALOG_DATA) public data: any, public readonly tableSettingsService: TableSettingsService) {
    this.tableType = data.tableType;
    this.title = data.title;
    this.defaultColumns = data.defaultColumns;
    this.tableDisplayedColumnsRef = data.displayedColumns;

      this.tableDisplayedColumnsRef.map(column => {
          this.columnSelection.set(column, true);
      })

  }

  save() {
      let newArray = [];

      for(let column of this.defaultColumns) {
          if(this.columnSelection.get(column)) {
            let index = this.defaultColumns.indexOf(column)
              newArray[index]= column;
          }
      }
      newArray = newArray.filter(item => item !== undefined);
      console.log(newArray);
      this.tableDisplayedColumnsRef.splice(0, this.tableDisplayedColumnsRef.length, ...newArray);
      this.tableSettingsService.setColumnVisibilitySettings(this.tableType, newArray);
      this.dialogRef.close();

  }

    handleCheckBoxChange (item: string, isChecked: boolean) {
    this.columnSelection.set(item,isChecked);
  }

  handleListItemClick( item: string) {
   // let element = event.target as HTMLElement;

    this.selectedColumn = item;
    console.log(this.selectedColumn);
  }

  handleSortListItem() {
    if(!this.selectedColumn) {
      console.log("no list item was selected")
      // TODO handle if no listitem was selected
      return;
    }
  }

  // TODO Reset button to reset sorting and displayedColumns
  selectAll(isChecked: boolean) {

    for(let column of this.defaultColumns) {
      this.columnSelection.set(column,isChecked);
    }
    this.selectAllSelected = true;

  }

}

