/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.caseimport.server;

import java.util.Objects;

/**
 * @author Abdelsalem Hedhili <abdelsalem.hedhili at rte-france.com>
 */
public class CaseImportException extends RuntimeException {

    public enum Type {
        IMPORT_CASE_FAILED,
        INCORRECT_CASE_FILE,
        REMOTE_ERROR,
    }

    private final Type type;

    public CaseImportException(Type type) {
        super(Objects.requireNonNull(type.name()));
        this.type = type;
    }

    public CaseImportException(Type type, String message) {
        super(message);
        this.type = type;
    }

    Type getType() {
        return type;
    }
}
