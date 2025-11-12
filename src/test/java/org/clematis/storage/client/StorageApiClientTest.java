package org.clematis.storage.client;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.clematis.storage.client.dto.FileMetadata;
import org.clematis.storage.client.dto.StorageEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

class StorageApiClientTest {

    private StorageApiClient client;

    @BeforeEach
    void setup() {
        client = Mockito.mock(StorageApiClient.class);
    }

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    @Test
    void testGetStorageEntity() {
        StorageEntity entity = new StorageEntity();
        entity.setId("abc123");
        entity.setFileName("photo.png");
        entity.setContentType("image/png");

        when(client.getStorageEntity("abc123")).thenReturn(entity);

        StorageEntity result = client.getStorageEntity("abc123");

        assertEquals("photo.png", result.getFileName());
        assertEquals("image/png", result.getContentType());
        verify(client, times(1)).getStorageEntity("abc123");
    }

    @SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
    @Test
    void testUploadFile() {
        MultipartFile mockFile = mock(MultipartFile.class);
        FileMetadata meta = new FileMetadata();
        meta.setFileName("uploaded.png");
        meta.setFileType("image/png");
        meta.setFileSize(2048L);

        when(client.uploadFile(eq(mockFile), eq("/images"))).thenReturn(meta);

        FileMetadata response = client.uploadFile(mockFile, "/images");

        assertEquals("uploaded.png", response.getFileName());
        assertEquals("image/png", response.getFileType());
        assertTrue(response.getFileSize() > 0);
        verify(client).uploadFile(mockFile, "/images");
    }

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    @Test
    void testDeleteFile() {
        doNothing().when(client).deleteFile("123");

        client.deleteFile("123");

        verify(client, times(1)).deleteFile("123");
    }

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    @Test
    void testGetProfileReturnsMap() {
        Map<String, Object> mockResponse = Map.of("version", "1.0.0");
        when(client.getProfile()).thenReturn(mockResponse);

        Map<String, Object> result = client.getProfile();

        assertEquals("1.0.0", result.get("version"));
        verify(client).getProfile();
    }
}
