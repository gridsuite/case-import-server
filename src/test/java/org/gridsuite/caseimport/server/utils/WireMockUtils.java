/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.caseimport.server.utils;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.ContainsPattern;

import static com.github.tomakehurst.wiremock.client.WireMock.matching;

/**
 * @author Abdelsalem Hedhili <abdelsalem.hedhili at rte-france.com>
 */
public class WireMockUtils {

    public static final String URI_CASE = "/v1/cases";

    public static final String URI_DIRECTORY_ELEMENTS = "/v1/directories/paths/elements";

    private final WireMockServer wireMock;

    public WireMockUtils(WireMockServer wireMock) {
        this.wireMock = wireMock;
    }

    public void stubImportCaseWithErrorInvalid(String filename) {
        wireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(URI_CASE))
                .withRequestBody(new ContainsPattern("filename=\"" + filename + "\""))
                .willReturn(WireMock.aResponse().withStatus(409).withBody("invalid file"))
        );
    }

    public void stubImportCaseWithErrorBadExtension(String filename) {
        wireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(URI_CASE))
                .withRequestBody(new ContainsPattern("filename=\"" + filename + "\""))
                .willReturn(WireMock.aResponse().withStatus(422).withBody("file with bad extension"))
        );
    }

    public void stubImportCase(String filename) {
        wireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(URI_CASE))
                .withRequestBody(new ContainsPattern("filename=\"" + filename + "\""))
                .willReturn(WireMock.ok())
        );
    }

    public void stubAddDirectoryElement(String path) {
        wireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(URI_DIRECTORY_ELEMENTS))
                        .withQueryParam("directoryPath", matching(path))
                .willReturn(WireMock.ok())
        );
    }
}
