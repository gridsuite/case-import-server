/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.gridsuite.caseimport.server;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.util.Map;

/**
 * @author Charaf EL MHARI <charaf.elmhari at rte-france.com>
 */

@Setter
@Getter
@ConfigurationPropertiesScan
@ConfigurationProperties("case-import-server")
public class CaseImportConfig {
    private Map<String, String> targetDirectories;
}
