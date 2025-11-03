/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.caseimport.server;

import org.gridsuite.caseimport.server.dto.ElementAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Abdelsalem HEDHILI <abdelsalem.hedhili at rte-france.com>
 */
@Service
public class DirectoryService {

    private static final String DIRECTORY_SERVER_API_VERSION = "v1";

    private static final String DELIMITER = "/";

    private static final String ROOT_DIRECTORIES_SERVER_ROOT_PATH = DELIMITER + DIRECTORY_SERVER_API_VERSION + DELIMITER
            + "directories";

    private final RestTemplate restTemplate;
    private String directoryServerBaseUri;

    private static final String HEADER_USER_ID = "userId";
    public static final String ELEMENT = "ELEMENT";

    public DirectoryService(
            @Value("${gridsuite.services.directory-server.base-uri:http://directory-server/}") String directoryServerBaseUri,
            RestTemplateBuilder restTemplateBuilder) {
        this.directoryServerBaseUri = directoryServerBaseUri;
        this.restTemplate = restTemplateBuilder.uriTemplateHandler(new DefaultUriBuilderFactory(directoryServerBaseUri)).build();
    }

    public void setDirectoryServerBaseUri(String directoryServerBaseUri) {
        this.directoryServerBaseUri = directoryServerBaseUri;
    }

    public void createElementInDirectory(ElementAttributes elementAttributes, String directoryName, String userId) {
        String path = UriComponentsBuilder
                .fromPath(ROOT_DIRECTORIES_SERVER_ROOT_PATH + "/paths/elements")
                .queryParam("directoryPath", directoryName)
                .build()
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_USER_ID, userId);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ElementAttributes> httpEntity = new HttpEntity<>(elementAttributes, headers);
        restTemplate
                .exchange(directoryServerBaseUri + path, HttpMethod.POST, httpEntity, ElementAttributes.class);
    }
}
