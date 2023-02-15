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

const MAPBOX_ACCESS_TOKEN =
  'pk.eyJ1IjoiZmVsaXhnZXJiaWciLCJhIjoiY2sxNmh4d2dvMTJkdTNpcGZtcWhvaHpuNyJ9.2hJW4R6PoiqIgytqUn1kbg';

export const _environment = {
  production: false,
  mockService: true,
  authDisabled: true,
  keycloakUrl: 'http://localhost:8080/',
  clientId: 'Cl17-CX-Part',
  defaultRealm: 'CX-Central',
  apiUrl: '/api',
  baseUrl: '/',
  mapStyles: 'mapbox://styles/mapbox/light-v10',
  customProtocols: {
    mapbox: {
      '//fonts/mapbox': {
        pathname: 'https://api.mapbox.com/fonts/v1/mapbox',
        queryParams: {
          access_token: MAPBOX_ACCESS_TOKEN,
        },
      },
      '//mapbox.': {
        pathname: 'https://api.mapbox.com/v4/mapbox.',
        postfix: '.json',
        queryParams: {
          secure: '',
          access_token: MAPBOX_ACCESS_TOKEN,
        },
      },
      '//styles/': {
        pathname: 'https://api.mapbox.com/styles/v1/',
        queryParams: {
          access_token: MAPBOX_ACCESS_TOKEN,
        },
      },
      '//sprites/mapbox/light-v10': {
        pathname: 'https://api.mapbox.com/styles/v1/mapbox/light-v10/sprite',
        queryParams: {
          access_token: MAPBOX_ACCESS_TOKEN,
        },
      },
    },
  },
};
