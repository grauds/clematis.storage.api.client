package org.clematis.storage.client.dto;

import lombok.Data;

@Data
public class StorageEntity {
    private String id;
    private String fileName;
    private String contentType;
    private byte[] data;
}
