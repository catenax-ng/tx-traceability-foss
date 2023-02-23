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

import { NotificationStatus } from '@shared/model/notification.model';
import { renderDeclineModal } from '@shared/modules/notification/modal/modalTestHelper.spec';
import { fireEvent, screen, waitFor } from '@testing-library/angular';

describe('DeclineNotificationModalComponent', () => {
  it('should create close modal', async () => {
    await renderDeclineModal(NotificationStatus.ACKNOWLEDGED);
    const title = await waitFor(() => screen.getByText('commonInvestigation.modal.declineTitle'));
    const hint = await waitFor(() => screen.getByText('commonInvestigation.modal.declineDescription'));
    const hint2 = await waitFor(() => screen.getByText('commonInvestigation.modal.declineReasonHint'));
    const buttonL = await waitFor(() => screen.getByText('actions.cancel'));
    const buttonR = await waitFor(() => screen.getByText('actions.decline'));

    expect(title).toBeInTheDocument();
    expect(hint).toBeInTheDocument();
    expect(hint2).toBeInTheDocument();
    expect(buttonL).toBeInTheDocument();
    expect(buttonR).toBeInTheDocument();
  });

  it('should render investigation description', async () => {
    const { notification } = await renderDeclineModal(NotificationStatus.ACKNOWLEDGED);
    const description = await waitFor(() => screen.getByText(notification.description));

    expect(description).toBeInTheDocument();
  });

  it('should check validation of textarea', async () => {
    await renderDeclineModal(NotificationStatus.ACKNOWLEDGED);
    fireEvent.click(await waitFor(() => screen.getByText('actions.decline')));

    const textArea: HTMLTextAreaElement = await waitFor(() => screen.getByTestId('TextAreaComponent-0'));

    const errorMessage_1 = await waitFor(() => screen.getByText('errorMessage.required'));
    expect(errorMessage_1).toBeInTheDocument();

    fireEvent.input(textArea, { target: { value: 'Some Text' } });

    expect(errorMessage_1).not.toBeInTheDocument();
  });

  it('should call close function', async () => {
    await renderDeclineModal(NotificationStatus.ACKNOWLEDGED);

    const textArea: HTMLTextAreaElement = await waitFor(() => screen.getByTestId('TextAreaComponent-0'));
    fireEvent.input(textArea, { target: { value: 'Some Text Some Text Some Text' } });
    fireEvent.click(await waitFor(() => screen.getByText('actions.decline')));

    await waitFor(() => expect(screen.getByText('commonInvestigation.modal.successfullyDeclined')).toBeInTheDocument());
  });
});
