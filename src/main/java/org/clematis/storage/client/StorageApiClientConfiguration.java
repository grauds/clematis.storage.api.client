package org.clematis.storage.client;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * Configuration for {@link StorageApiClient} including MDC transaction ID propagation.
 */
public class StorageApiClientConfiguration {

    /**
     * Default header name used to propagate the transaction ID.
     */
    public static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";

    /**
     * Default key checked in SLF4J MDC context.
     */
    public static final String MDC_TRANSACTION_ID_KEY = "transactionId";

    /**
     * Creates a Feign {@link RequestInterceptor} that extracts transaction ID from MDC
     * and sets it as the {@code X-Transaction-Id} header on outgoing requests.
     *
     * @return the request interceptor
     */
    @Bean
    public RequestInterceptor transactionIdRequestInterceptor() {
        return (RequestTemplate template) -> {
            String transactionId = MDC.get(MDC_TRANSACTION_ID_KEY);
            if (transactionId == null || transactionId.isBlank()) {
                transactionId = MDC.get(TRANSACTION_ID_HEADER);
            }
            if (transactionId == null || transactionId.isBlank()) {
                transactionId = MDC.get("transaction_id");
            }
            if (transactionId != null && !transactionId.isBlank()) {
                template.header(TRANSACTION_ID_HEADER, transactionId);
            }
        };
    }
}
