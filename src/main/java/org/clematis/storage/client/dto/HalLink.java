package org.clematis.storage.client.dto;

import lombok.Data;

@Data
public class HalLink {
    private String href;
    private String hreflang;
    private String title;
    private String type;
    private String deprecation;
    private String profile;
    private String name;
    private boolean templated;
}
