package org.clematis.storage.client;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@TestConfiguration
@EnableFeignClients(basePackageClasses = StorageApiClient.class)
public class FeignTestConfig {

    @Bean
    public String baseUrl() {
        return "http://localhost";
    }
}
