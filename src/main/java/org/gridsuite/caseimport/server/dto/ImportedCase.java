/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.caseimport.server.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * @author Charaf EL MHARI <charaf.elmhari at rte-france.com>
 */

@Setter
@Getter
public class ImportedCase {
    private UUID caseUuid;
    private String caseName;
    private String parentDirectory;
}
