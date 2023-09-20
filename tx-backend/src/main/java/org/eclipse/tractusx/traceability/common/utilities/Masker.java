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

package org.eclipse.tractusx.traceability.common.utilities;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class Masker {

    public static final int UNMASKED_LENGTH = 4;

    public static String mask(final String stringToMask) {
        if (StringUtils.isBlank(stringToMask) || StringUtils.length(stringToMask) <= UNMASKED_LENGTH) {
            return "****"; // mask everything
        }
        // mask everything after the first 4 characters
        final String mask = StringUtils.repeat("*", stringToMask.length() - UNMASKED_LENGTH);
        return StringUtils.overlay(stringToMask, mask, UNMASKED_LENGTH, stringToMask.length());
    }

    public static String mask(final Object object) {
        return mask(object.toString());
    }
}
