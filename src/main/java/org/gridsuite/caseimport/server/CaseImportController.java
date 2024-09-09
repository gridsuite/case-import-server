/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.caseimport.server;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gridsuite.caseimport.server.dto.ImportedCase;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Abdelsalem HEDHILI <abdelsalem.hedhili at rte-france.com>
 */
@RestController
@RequestMapping(value = "/" + CaseImportApi.API_VERSION + "/")
@Tag(name = "case-import-server")
@ComponentScan(basePackageClasses = CaseImportService.class)
public class CaseImportController {

    private final CaseImportService caseImportService;

    public CaseImportController(CaseImportService caseImportService) {
        this.caseImportService = caseImportService;
    }

    @PostMapping(value = "/cases/{caseName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Import a case in the parametrized directory")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "The case is imported"),
        @ApiResponse(responseCode = "400", description = "Invalid case file"),
        @ApiResponse(responseCode = "422", description = "File with wrong extension"),
        @ApiResponse(responseCode = "201", description = "Case created successfully")})
    public ResponseEntity<ImportedCase> importCase(@Parameter(description = "case file") @RequestPart("caseFile") MultipartFile caseFile,
                                                   @Parameter(description = "name of the case") @PathVariable String caseName,
                                                   @Parameter(description = "origin of case file") @RequestParam(defaultValue = "default", required = false) String caseFileSource,
                                                   @RequestHeader("userId") String userId) {
        ImportedCase importedCase = caseImportService.importCaseInDirectory(caseFile, caseName, caseFileSource, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(importedCase);
    }
}
