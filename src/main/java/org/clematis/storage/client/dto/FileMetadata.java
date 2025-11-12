package org.clematis.storage.client.dto;

import lombok.Data;

@Data
public class FileMetadata {
    private String fileName;
    private String downloadUrl;
    private String fileType;
    private long fileSize;
}
