package org.clematis.storage.client.dto;

import java.util.List;

import lombok.Data;

@Data
public class PageResponse<T> {
    private List<T> content;
    private int size;
    private long totalElements;
    private int totalPages;
    private int number;
}
