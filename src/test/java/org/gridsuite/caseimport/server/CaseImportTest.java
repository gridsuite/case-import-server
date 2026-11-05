/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.caseimport.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.gridsuite.caseimport.server.utils.WireMockUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.io.InputStream;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.gridsuite.caseimport.server.utils.WireMockUtils.CASE_UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Abdelsalem Hedhili <abdelsalem.hedhili at rte-france.com>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class CaseImportTest {
    private static final String TEST_FILE = "testCase.xiidm";
    private static final String TEST_CASE_NAME = "testCase";
    private static final String TEST_FILE_WITH_ERRORS = "testCase_with_errors.xiidm";
    private static final String DEFAULT_IMPORT_DIRECTORY = "Automatic_cases_import";
    private static final String INVALID_CASE_ORIGIN = "invalid_source";
    private static final String CASE_ORIGIN_1 = "origin1";
    private static final String CASE_ORIGIN_1_DIRECTORY = "case_import_directory_1";

    private static final String TEST_INCORRECT_FILE = "application-default.yml";
    private static final String USER1 = "user1";

    private WireMockServer wireMockServer;

    private WireMockUtils wireMockUtils;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DirectoryService directoryService;
    @Autowired
    private CaseService caseService;
    @Autowired
    private OutputDestination outputDestination;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    void initialize() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockUtils = new WireMockUtils(wireMockServer);

        // Start the server.
        wireMockServer.start();
    }

    @AfterAll
    void tearDown() {
        wireMockServer.shutdown();
    }

    @BeforeEach
    void setup() {
        wireMockServer.resetAll();
        directoryService.setDirectoryServerBaseUri(wireMockServer.baseUrl());
        caseService.setBaseUri(wireMockServer.baseUrl());
    }

    @AfterEach
    void checkNoRemainingNotifications() {
        assertNull(outputDestination.receive(100, "caseimport.imported"));
    }

    private void assertCaseImportedNotification(String caseOrigin) throws Exception {
        Message<byte[]> notification = outputDestination.receive(1000, "caseimport.imported");
        assertNotNull(notification);
        assertEquals(caseOrigin, notification.getHeaders().get("caseOrigin"));
        JsonNode payload = objectMapper.readTree(notification.getPayload());
        assertEquals(CASE_UUID, payload.asText());
    }

    @AfterAll
    void cleanup() {
        wireMockServer.checkForUnmatchedRequests();
    }

    @Test
    void testImportCase() throws Exception {
        wireMockUtils.stubImportCase(TEST_FILE);
        wireMockUtils.stubAddDirectoryElement(DEFAULT_IMPORT_DIRECTORY);
        try (InputStream is = new FileInputStream(ResourceUtils.getFile("classpath:" + TEST_FILE))) {
            MockMultipartFile mockFile = new MockMultipartFile("caseFile", TEST_FILE, MediaType.TEXT_XML_VALUE, is);

            mockMvc.perform(multipart("/v1/cases").file(mockFile)
                            .header("userId", USER1)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                    )
                    .andExpect(status().isCreated());
        }
        assertCaseImportedNotification("default");
    }

    @Test
    void testImportCaseWithBadRequestError() throws Exception {
        wireMockUtils.stubImportCaseWithErrorInvalid(TEST_FILE_WITH_ERRORS);
        wireMockUtils.stubAddDirectoryElement(DEFAULT_IMPORT_DIRECTORY);
        try (InputStream is = new FileInputStream(ResourceUtils.getFile("classpath:" + TEST_FILE))) {
            MockMultipartFile mockFile = new MockMultipartFile("caseFile", TEST_FILE_WITH_ERRORS, MediaType.TEXT_XML_VALUE, is);

            mockMvc.perform(multipart("/v1/cases").file(mockFile)
                            .header("userId", USER1)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                    )
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    void testImportCaseWithUnprocessableEntityError() throws Exception {
        wireMockUtils.stubImportCaseWithErrorBadExtension(TEST_INCORRECT_FILE);
        wireMockUtils.stubAddDirectoryElement(DEFAULT_IMPORT_DIRECTORY);
        try (InputStream is = new FileInputStream(ResourceUtils.getFile("classpath:" + TEST_FILE))) {
            MockMultipartFile mockFile = new MockMultipartFile("caseFile", TEST_INCORRECT_FILE, MediaType.TEXT_XML_VALUE, is);

            mockMvc.perform(multipart("/v1/cases").file(mockFile)
                            .header("userId", USER1)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                    )
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Test
    void testImportCaseWithInvalidOrigin() throws Exception {
        wireMockUtils.stubImportCaseWithErrorInvalid(TEST_FILE);
        wireMockUtils.stubAddDirectoryElement(DEFAULT_IMPORT_DIRECTORY);
        try (InputStream is = new FileInputStream(ResourceUtils.getFile("classpath:" + TEST_FILE))) {
            MockMultipartFile mockFile = new MockMultipartFile("caseFile", TEST_FILE, MediaType.TEXT_XML_VALUE, is);

            mockMvc.perform(multipart("/v1/cases").file(mockFile)
                            .header("userId", USER1)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .param("caseFileSource", INVALID_CASE_ORIGIN)
                    )
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Test
    void testImportCaseWithValidOrigin() throws Exception {
        final String caseName = "testCase";
        wireMockUtils.stubImportCase(TEST_FILE);
        wireMockUtils.stubAddDirectoryElement(CASE_ORIGIN_1_DIRECTORY);
        try (InputStream is = new FileInputStream(ResourceUtils.getFile("classpath:" + TEST_FILE))) {
            MockMultipartFile mockFile = new MockMultipartFile("caseFile", TEST_FILE, MediaType.TEXT_XML_VALUE, is);

            mockMvc.perform(multipart("/v1/cases").file(mockFile)
                            .header("userId", USER1)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .param("caseFileSource", CASE_ORIGIN_1)
                            .param("caseName", caseName)
                    )
                    .andExpectAll(status().isCreated(),
                            jsonPath("caseName").value(caseName),
                            jsonPath("parentDirectory").value(CASE_ORIGIN_1_DIRECTORY));
        }
        assertCaseImportedNotification(CASE_ORIGIN_1);
    }

    @Test
    void testGivenEmptyCaseNameUseFilename() throws Exception {
        wireMockUtils.stubImportCase(TEST_FILE);
        wireMockUtils.stubAddDirectoryElement(CASE_ORIGIN_1_DIRECTORY);
        try (InputStream is = new FileInputStream(ResourceUtils.getFile("classpath:" + TEST_FILE))) {
            MockMultipartFile mockFile = new MockMultipartFile("caseFile", TEST_FILE, MediaType.TEXT_XML_VALUE, is);

            mockMvc.perform(multipart("/v1/cases").file(mockFile)
                            .header("userId", USER1)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .param("caseFileSource", CASE_ORIGIN_1)
                            .param("caseName", "")
                    )
                    .andExpectAll(status().isCreated(),
                            jsonPath("caseName").value(TEST_CASE_NAME),
                            jsonPath("parentDirectory").value(CASE_ORIGIN_1_DIRECTORY));
        }
        assertCaseImportedNotification(CASE_ORIGIN_1);
    }
}
