/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.caseimport.server;

import org.gridsuite.caseimport.server.dto.AccessRightsAttributes;
import org.gridsuite.caseimport.server.dto.ElementAttributes;
import org.gridsuite.caseimport.server.dto.ImportedCase;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.gridsuite.caseimport.server.CaseImportException.Type.UNKNOWN_CASE_SOURCE;

/**
 * @author Abdelsalem HEDHILI <abdelsalem.hedhili at rte-france.com>
 */
@Service
class CaseImportService {

    private final CaseImportConfig caseImportConfig;

    private final DirectoryService directoryService;

    private final CaseService caseService;

    static final String CASE = "CASE";

    public CaseImportService(CaseService caseService, DirectoryService directoryService, CaseImportConfig caseImportConfig) {
        this.caseService = caseService;
        this.directoryService = directoryService;
        this.caseImportConfig = caseImportConfig;
    }

    private String getTargetDirectory(String caseOrigin) {
        String targetDirectory = caseImportConfig.getTargetDirectories().get(caseOrigin);
        if (targetDirectory == null) {
            throw new CaseImportException(UNKNOWN_CASE_SOURCE, "Unknown case origin " + caseOrigin);
        }
        return targetDirectory;
    }

    ImportedCase importCaseInDirectory(MultipartFile caseFile, String caseName, String caseOrigin, String userId) {
        String targetDirectory = getTargetDirectory(caseOrigin);
        UUID caseUuid = caseService.importCase(caseFile);
        var caseElementAttributes = new ElementAttributes(caseUuid, caseName, CASE, new AccessRightsAttributes(false), userId, 0L, null);
        directoryService.createElementInDirectory(caseElementAttributes, targetDirectory, userId);
        ImportedCase importedCase = new ImportedCase();
        importedCase.setCaseName(caseElementAttributes.getElementName());
        importedCase.setCaseUuid(caseElementAttributes.getElementUuid());
        importedCase.setParentDirectory(targetDirectory);
        return importedCase;
    }
}
