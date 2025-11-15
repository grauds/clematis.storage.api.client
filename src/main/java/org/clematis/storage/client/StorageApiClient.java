package org.clematis.storage.client;

import java.util.List;
import java.util.Map;

import org.clematis.storage.client.dto.FileMetadata;
import org.clematis.storage.client.dto.PageResponse;
import org.clematis.storage.client.dto.StorageEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(
    name = "storageApiClient",
    url = "${storage.api.url}"
)
public interface StorageApiClient {

    // ---- Profile ----
    @GetMapping(value = "/api/profile", produces = "application/hal+json")
    Map<String, Object> getProfile();

    @GetMapping(value = "/api/profile/storageEntities", produces = "application/alps+json")
    String getStorageEntitiesDescriptor();

    // ---- Storage Entities ----
    @GetMapping(value = "/api/storageEntities", produces = "application/hal+json")
    PageResponse<StorageEntity> getStorageEntitiesPaged(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(required = false) List<String> sort
    );

    @PostMapping(value = "/api/storageEntities", consumes = "application/json", produces = "application/hal+json")
    StorageEntity createStorageEntity(@RequestBody StorageEntity request);

    @GetMapping(value = "/api/storageEntities/{id}", produces = "application/hal+json")
    StorageEntity getStorageEntity(@PathVariable("id") String id);

    @PutMapping(value = "/api/storageEntities/{id}", consumes = "application/json", produces = "application/hal+json")
    StorageEntity updateStorageEntity(@PathVariable("id") String id, @RequestBody StorageEntity request);

    @PatchMapping(value = "/api/storageEntities/{id}", consumes = "application/json", produces = "application/hal+json")
    StorageEntity patchStorageEntity(@PathVariable("id") String id, @RequestBody StorageEntity request);

    @DeleteMapping("/api/storageEntities/{id}")
    void deleteStorageEntity(@PathVariable("id") String id);

    // ---- Files ----
    @GetMapping(value = "/api/files/get", produces = "application/hal+json")
    List<FileMetadata> getAllFiles();

    @GetMapping(value = "/api/files/getByPath", produces = "application/hal+json")
    List<FileMetadata> getFilesByPath(@RequestParam String pathPrefix);

    @GetMapping(value = "/api/files/download/{id}", produces = "application/hal+json")
    FileMetadata getFile(@PathVariable("id") String id);

    @PostMapping(value = "/api/files/upload", consumes = "multipart/form-data", produces = "application/hal+json")
    FileMetadata uploadFile(@RequestPart("file") MultipartFile file,
                            @RequestParam(required = false) String path);

    @PostMapping(value = "/api/files/upload/batch", consumes = "multipart/form-data", produces = "application/hal+json")
    List<FileMetadata> uploadFilesBatch(@RequestPart("files") List<MultipartFile> files,
                                        @RequestParam(required = false) String path);

    @DeleteMapping("/api/files/{id}")
    void deleteFile(@PathVariable("id") String id);

    // ---- DB Files ----
    @GetMapping(value = "/api/db/get", produces = "application/hal+json")
    List<FileMetadata> getAllDbFiles();

    @GetMapping(value = "/api/db/getByPath", produces = "application/hal+json")
    List<FileMetadata> getDbFilesByPath(@RequestParam String pathPrefix);

    @GetMapping(value = "/api/db/download/{id}", produces = "application/hal+json")
    FileMetadata getDbFile(@PathVariable("id") String id);

    @PostMapping(value = "/api/db/upload", consumes = "multipart/form-data", produces = "application/hal+json")
    FileMetadata uploadDbFile(@RequestPart("file") MultipartFile file,
                              @RequestParam(required = false) String path);

    @PostMapping(value = "/api/db/upload/batch", consumes = "multipart/form-data", produces = "application/hal+json")
    List<FileMetadata> uploadDbFilesBatch(@RequestPart("files") List<MultipartFile> files,
                                          @RequestParam(required = false) String path);

    @DeleteMapping("/api/db/{id}")
    void deleteDbFile(@PathVariable("id") String id);
}