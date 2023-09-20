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

import { OtherPartsState } from '@page/other-parts/core/other-parts.state';
import { OtherPartsModule } from '@page/other-parts/other-parts.module';
import { PartsState } from '@page/parts/core/parts.state';
import { fireEvent, screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

import { CustomerPartsComponent } from './customer-parts.component';

describe('CustomerPartsComponent', () => {
  let otherPartsState: OtherPartsState;
  beforeEach(() => (otherPartsState = new OtherPartsState()));

  const renderCustomerParts = () =>
    renderComponent(CustomerPartsComponent, {
      imports: [OtherPartsModule],
      providers: [{ provide: OtherPartsState, useFactory: () => otherPartsState }, { provide: PartsState }],
      roles: ['admin', 'wip'],
    });

  it('should render part table', async () => {
    await renderCustomerParts();

    const tableElements = await waitFor(() => screen.getAllByTestId('table-component--test-id'));
    expect(tableElements.length).toEqual(1);
  });

  it('should render table and display correct amount of rows', async () => {
    await renderCustomerParts();
    const tableElement = await waitFor(() => screen.getByTestId('table-component--test-id'));
    expect(tableElement).toBeInTheDocument();
    expect(tableElement.children[1].childElementCount).toEqual(5);
  });

  it('sort customer parts after name column', async () => {
    const {fixture} = await renderCustomerParts();
    const customerPartsComponent = fixture.componentInstance;

    let nameHeader = await screen.findByText('table.column.name');
    fireEvent.click(nameHeader);

    expect(customerPartsComponent['tableCustomerSortList']).toEqual([["name", "asc"]]);

  });

  it('should multisort after column name and semanticModelId', async () => {
    const {fixture} = await renderCustomerParts();
    const customerPartsComponent = fixture.componentInstance;

    let nameHeader = await screen.findByText('table.column.name');
    fireEvent.click(nameHeader);
    let semanticModelIdHeader = await screen.findByText('table.column.semanticModelId')

    await waitFor(() => {fireEvent.keyDown(semanticModelIdHeader, {
      ctrlKey: true,
      charCode: 17
    })})
    expect(customerPartsComponent['ctrlKeyState']).toBeTruthy();
    await waitFor(() => {
      fireEvent.click(semanticModelIdHeader)
    });

    await waitFor(() => {fireEvent.keyUp(semanticModelIdHeader, {
      ctrlKey: true,
      charCode: 17
    })})

    await waitFor(() => {fireEvent.click(semanticModelIdHeader)});
    expect(customerPartsComponent['tableCustomerSortList']).toEqual([["name", "asc"], ["semanticModelId", "desc"]]);
  });

  it('should reset sorting on third click', async () => {
    const {fixture} = await renderCustomerParts();
    const customerPartsComponent = fixture.componentInstance;

    let nameHeader = await screen.findByText('table.column.name');
    fireEvent.click(nameHeader);
    let semanticModelIdHeader = await screen.findByText('table.column.semanticModelId')

    await waitFor(() => {fireEvent.keyDown(semanticModelIdHeader, {
      ctrlKey: true,
      charCode: 17
    })})
    expect(customerPartsComponent['ctrlKeyState']).toBeTruthy();
    await waitFor(() => {
      fireEvent.click(semanticModelIdHeader)
    });

    await waitFor(() => {fireEvent.keyUp(semanticModelIdHeader, {
      ctrlKey: true,
      charCode: 17
    })})

    await waitFor(() => {fireEvent.click(semanticModelIdHeader)});
    await waitFor(() => {fireEvent.click(semanticModelIdHeader)});
    expect(customerPartsComponent['tableCustomerSortList']).toEqual([]);
  });

});
