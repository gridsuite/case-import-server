/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.caseimport.server;

import org.gridsuite.caseimport.server.dto.AccessRightsAttributes;
import org.gridsuite.caseimport.server.dto.ElementAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * @author Abdelsalem HEDHILI <abdelsalem.hedhili at rte-france.com>
 */
@Service
class CaseImportService {

    @Value("${target-directory-name: CVG_Recollement}")
    private String directoryName;

    private final DirectoryService directoryService;

    private final CaseService caseService;

    static final String CASE = "CASE";

    public CaseImportService(CaseService caseService, DirectoryService directoryService) {
        this.caseService = caseService;
        this.directoryService = directoryService;
    }

    void importCaseInDirectory(MultipartFile caseFile, String userId) {
        UUID caseUuid = caseService.importCase(caseFile);
        var caseElementAttributes = new ElementAttributes(caseUuid, caseFile.getOriginalFilename(), CASE, new AccessRightsAttributes(false), userId, 0L, null);
        directoryService.createDirectory(caseElementAttributes, directoryName, userId);
    }
}
