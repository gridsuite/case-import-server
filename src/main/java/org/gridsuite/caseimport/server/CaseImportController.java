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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
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

    @Autowired
    private CaseImportService caseImportService;

    @PostMapping(value = "/cases", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Import a case in the parametrized directory")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "The case is imported"),
        @ApiResponse(responseCode = "400", description = "Invalid case file"),
        @ApiResponse(responseCode = "422", description = "File with wrong extension")})
    public ResponseEntity<Void> importCase(@Parameter(description = "case file") @RequestPart("caseFile") MultipartFile caseFile,
                                           @RequestHeader("userId") String userId) {
        caseImportService.importCaseInDirectory(caseFile, userId);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
    }
}
