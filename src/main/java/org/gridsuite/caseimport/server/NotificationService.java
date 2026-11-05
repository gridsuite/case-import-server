/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.caseimport.server;

import org.gridsuite.caseimport.server.dto.ImportedCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
class NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    private final StreamBridge caseImportPublisher;

    NotificationService(StreamBridge caseImportPublisher) {
        this.caseImportPublisher = caseImportPublisher;
    }

    void emitCaseImported(ImportedCase importedCase, String caseOrigin) {
        Message<ImportedCase> message = MessageBuilder.withPayload(importedCase)
                .setHeader("caseOrigin", caseOrigin)
                .build();
        LOGGER.debug("Sending message: {}", message);
        caseImportPublisher.send("publishCaseImported-out-0", message);
    }
}
