import { Component, Inject } from '@angular/core';
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
  title: string;
  tableColumnsRef: string[];
  ALL_COLLUMS_IN_DEFAULT_ORDER: string[];
  //tableDisplayedFilterColumnsRef: string[];

  tableType: PartTableType;

  columnsCheckBoxMap: Map<string,boolean> = new Map<string, boolean>();
  columnsShownInDialog: string[];

  selectAllSelected: boolean;

  selectedColumn: string = null;




  constructor(public dialogRef: MatDialogRef<TableSettingsComponent>, @Inject(MAT_DIALOG_DATA) public data: any, public readonly tableSettingsService: TableSettingsService) {
    this.tableType = data.tableType;
    this.title = data.title;
    this.columnsShownInDialog = data.defaultColumns;
    this.tableColumnsRef = data.displayedColumns;

      this.tableColumnsRef.map(column => {
          this.columnsCheckBoxMap.set(column, true);
      })

    this.ALL_COLLUMS_IN_DEFAULT_ORDER = data.unchangedColumns;
    console.log("columnsInDialog", this.columnsShownInDialog);
    console.log("tableColumnsRef",this.tableColumnsRef);
    console.log("ALL_DEFAULT_UNSORTED",this.ALL_COLLUMS_IN_DEFAULT_ORDER);
    console.log("columnsCheckBoxMap", this.columnsCheckBoxMap);
  }

  save() {
      let newArray = [];

      for(let column of this.columnsShownInDialog) {
          if(this.columnsCheckBoxMap.get(column)) {
            let index = this.columnsShownInDialog.indexOf(column)
              newArray[index]= column;
          }
      }
      newArray = newArray.filter(item => item !== undefined);
      console.log(newArray);
      this.tableColumnsRef.splice(0, this.tableColumnsRef.length, ...newArray);
      this.tableSettingsService.setColumnVisibilitySettings(this.tableType, newArray);
      this.dialogRef.close();
    console.log("columnsInDialog", this.columnsShownInDialog);
    console.log("tableColumnsRef",this.tableColumnsRef);
    console.log("ALL_DEFAULT_UNSORTED",this.ALL_COLLUMS_IN_DEFAULT_ORDER);
    console.log("columnsCheckBoxMap", this.columnsCheckBoxMap);
  }

    handleCheckBoxChange (item: string, isChecked: boolean) {
    this.columnsCheckBoxMap.set(item,isChecked);
      console.log("columnsInDialog", this.columnsShownInDialog);
      console.log("tableColumnsRef",this.tableColumnsRef);
      console.log("ALL_DEFAULT_UNSORTED",this.ALL_COLLUMS_IN_DEFAULT_ORDER);
      console.log("columnsCheckBoxMap", this.columnsCheckBoxMap);
  }

  handleListItemClick( event: MouseEvent, item: string) {
   let element = event.target as HTMLElement;

   if(element.tagName !== 'INPUT') {
     this.selectedColumn = item;
     element.classList.toggle('selected-item');
     console.log(this.selectedColumn);
   }

    this.selectedColumn = item;
  }

  handleSortListItem(direction: string) {
    if(!this.selectedColumn) {
      console.log("no list item was selected")
      // TODO handle if no listitem was selected
      return;
    }

    let oldPosition = this.columnsShownInDialog.indexOf(this.selectedColumn);
    let newPositon = direction === 'up' ? -1 : 1;

    if((oldPosition == 0 && direction === 'up') || (oldPosition === this.columnsShownInDialog.length-1 && direction === 'down')) {
      return;
    }
      let temp = this.columnsShownInDialog[oldPosition+newPositon];
      this.columnsShownInDialog[oldPosition+newPositon] = this.selectedColumn;
      this.columnsShownInDialog[oldPosition] = temp;
    console.log("columnsInDialog", this.columnsShownInDialog);
    console.log("tableColumnsRef",this.tableColumnsRef);
    console.log("ALL_DEFAULT_UNSORTED",this.ALL_COLLUMS_IN_DEFAULT_ORDER);
    console.log("columnsCheckBoxMap", this.columnsCheckBoxMap);


  }


  selectAll(isChecked: boolean) {

    for(let column of this.columnsShownInDialog) {
      this.columnsCheckBoxMap.set(column,isChecked);
    }
    this.selectAllSelected = true;

    console.log("columnsInDialog", this.columnsShownInDialog);
    console.log("tableColumnsRef",this.tableColumnsRef);
    console.log("ALL_DEFAULT_UNSORTED",this.ALL_COLLUMS_IN_DEFAULT_ORDER);
    console.log("columnsCheckBoxMap", this.columnsCheckBoxMap);

  }

  resetColumns() {
    this.columnsShownInDialog.splice(0,this.columnsShownInDialog.length, ...this.ALL_COLLUMS_IN_DEFAULT_ORDER);
    this.selectAll(true);
    console.log("columnsInDialog", this.columnsShownInDialog);
    console.log("tableColumnsRef",this.tableColumnsRef);
    console.log("ALL_DEFAULT_UNSORTED",this.ALL_COLLUMS_IN_DEFAULT_ORDER);
    console.log("columnsCheckBoxMap", this.columnsCheckBoxMap);
  }

}

