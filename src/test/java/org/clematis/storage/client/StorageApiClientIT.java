package org.clematis.storage.client;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import org.clematis.storage.client.dto.FileMetadata;
import org.clematis.storage.client.dto.StorageEntity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

@SpringBootTest(classes = {TestApplication.class, FeignTestConfig.class})
@ContextConfiguration(initializers = StorageApiClientIT.MockServerInitializer.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StorageApiClientIT {

    // Create but don't start the server yet
    private static final MockWebServer MOCK_WEB_SERVER;

    // Static initializer to set up once before tests
    static {
        try {
            MOCK_WEB_SERVER = new MockWebServer();
            MOCK_WEB_SERVER.start();
        } catch (IOException e) {
            throw new RuntimeException("Could not start MockWebServer", e);
        }
    }

    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private StorageApiClient storageApiClient;

    @AfterAll
    public static void tearDownServer() throws IOException {
        MOCK_WEB_SERVER.shutdown();
    }

    @BeforeEach
    void setupTest() {
        // Clear the queue before each test
        MOCK_WEB_SERVER.getDispatcher();
    }

    @AfterEach
    void cleanupTest() {
        // Make sure we've consumed all responses to avoid blocking
        int count = MOCK_WEB_SERVER.getRequestCount();
        for (int i = 0; i < count; i++) {
            try {
                // Use a very short timeout to avoid blocking
                MOCK_WEB_SERVER.takeRequest(1, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                // Ignore
            }
        }
    }

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    @Test
    void testGetStorageEntityReturnsParsedObject() throws Exception {
        StorageEntity entity = new StorageEntity();
        entity.setId("id123");
        entity.setFileName("hello.txt");
        entity.setContentType("text/plain");
        entity.setData("Hi".getBytes());

        MOCK_WEB_SERVER.enqueue(new MockResponse()
            .setBody(mapper.writeValueAsString(entity))
            .addHeader("Content-Type", "application/json"));

        StorageEntity response = storageApiClient.getStorageEntity("id123");

        assertThat(response.getFileName()).isEqualTo("hello.txt");
        assertThat(response.getContentType()).isEqualTo("text/plain");
        assertThat(response.getId()).isEqualTo("id123");
    }

    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:MultipleStringLiterals"})
    @Test
    void testGetAllFilesReturnsList() throws Exception {
        FileMetadata f1 = new FileMetadata();
        f1.setFileName("a.txt");
        f1.setContentType("text/plain");
        f1.setSize(123);

        FileMetadata f2 = new FileMetadata();
        f2.setFileName("b.txt");
        f2.setContentType("text/plain");
        f2.setSize(456);

        MOCK_WEB_SERVER.enqueue(new MockResponse()
            .setBody(mapper.writeValueAsString(List.of(f1, f2)))
            .addHeader("Content-Type", "application/json"));

        List<FileMetadata> files = storageApiClient.getAllFiles();

        assertThat(files).hasSize(2);
        assertThat(files.get(0).getFileName()).isEqualTo("a.txt");
        assertThat(files.get(1).getSize()).isEqualTo(456);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    void testDeleteFileSendsDeleteRequest() throws Exception {
        // Enqueue response
        MOCK_WEB_SERVER.enqueue(new MockResponse().setResponseCode(204));

        // Make request
        storageApiClient.deleteFile("abc123");

        // Verify request
        RecordedRequest recorded = MOCK_WEB_SERVER.takeRequest(1, TimeUnit.SECONDS);
        assertThat(recorded.getMethod()).isEqualTo("DELETE");
        assertThat(recorded.getPath()).isEqualTo("/api/files/abc123");
    }

    /** Dynamically inject mock server URL into Spring’s environment */
    public static class MockServerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext ctx) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                ctx,
                "storage.api.url=http://localhost:" + MOCK_WEB_SERVER.getPort()
            );
        }
    }
}
