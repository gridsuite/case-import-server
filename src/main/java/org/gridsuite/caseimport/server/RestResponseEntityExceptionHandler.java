/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.caseimport.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Map;

/**
 * @author Abdelsalem Hedhili <abdelsalem.hedhili at rte-france.com>
 */
@ControllerAdvice
public class RestResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

    @ExceptionHandler(value = {CaseImportException.class})
    protected ResponseEntity<Object> handleCaseImportException(CaseImportException exception) {
        if (LOGGER.isErrorEnabled()) {
            LOGGER.error(exception.getMessage(), exception);
        }
        return switch (exception.getType()) {
            case REMOTE_ERROR -> new ResponseEntity<>(Map.of("message", exception.getMessage()), HttpStatus.BAD_REQUEST);
            case INCORRECT_CASE_FILE, UNKNOWN_CASE_SOURCE ->
                    new ResponseEntity<>(Map.of("message", exception.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        };
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<Object> handleAllException(Exception exception) {
        if (exception instanceof HttpStatusCodeException httpStatusCodeException) {
            return ResponseEntity.status(httpStatusCodeException.getStatusCode()).body(exception.getMessage());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }
}
