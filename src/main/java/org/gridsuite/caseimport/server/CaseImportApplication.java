/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.caseimport.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * @author Abdelsalem HEDHILI <abdelsalem.hedhili at rte-france.com>
 */
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@SpringBootApplication
@ConfigurationPropertiesScan
public class CaseImportApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaseImportApplication.class, args);
    }
}
