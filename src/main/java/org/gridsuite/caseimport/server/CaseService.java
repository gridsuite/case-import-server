/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.caseimport.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.UUID;

import static org.gridsuite.caseimport.server.CaseImportException.Type.INCORRECT_CASE_FILE;


/**
 * @author Abdelsalem Hedhili <abdelsalem.hedhili at rte-france.com>
 */
@Service
public class CaseService {
    private static final String CASE_SERVER_API_VERSION = "v1";

    private static final String DELIMITER = "/";
    private final RestTemplate restTemplate;
    private String caseServerBaseUri;

    @Autowired
    public CaseService(@Value("${powsybl.services.case-server.base-uri:http://case-server/}") String caseServerBaseUri,
                       RestTemplate restTemplate) {
        this.caseServerBaseUri = caseServerBaseUri;
        this.restTemplate = restTemplate;
    }

    public void setBaseUri(String caseServerBaseUri) {
        this.caseServerBaseUri = caseServerBaseUri;
    }

    UUID importCase(MultipartFile multipartFile) {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        UUID caseUuid;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        if (multipartFile != null) {
            multipartBodyBuilder.part("file", multipartFile.getResource())
                    .filename(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        }

        HttpEntity<MultiValueMap<String, HttpEntity<?>>> request = new HttpEntity<>(
                multipartBodyBuilder.build(), headers);
        try {
            caseUuid = restTemplate.postForObject(caseServerBaseUri + DELIMITER + CASE_SERVER_API_VERSION + "/cases", request,
                    UUID.class);
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().equals(HttpStatus.UNPROCESSABLE_ENTITY)) {
                throw new CaseImportException(INCORRECT_CASE_FILE, e.getMessage());
            }
            throw wrapRemoteError(e.getMessage(), e.getStatusCode());
        }
        return caseUuid;
    }

    private static CaseImportException wrapRemoteError(String response, HttpStatusCode statusCode) {
        if (!"".equals(response)) {
            throw new CaseImportException(CaseImportException.Type.REMOTE_ERROR, response);
        } else {
            throw new CaseImportException(CaseImportException.Type.REMOTE_ERROR, statusCode.toString());
        }
    }
}
