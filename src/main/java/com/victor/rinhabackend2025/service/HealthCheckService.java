package com.victor.rinhabackend2025.service;

import com.victor.rinhabackend2025.dto.ServiceHealthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class HealthCheckService {

    private static final Logger log = LoggerFactory.getLogger(HealthCheckService.class);
    @Value("${default-url}")
    private String DEFAULT_URL;
    @Value("${fallback-url}")
    private String FALLBACK_URL;
    private final AtomicReference<String> currentProcessor = new AtomicReference<>("default");
    private final RestClient restClient;

    public HealthCheckService(RestClient restClient) {
        this.restClient = restClient;
    }

    @Scheduled(fixedRate = 5000)
    public String chooseProcessor(){
        if (isServiceHealth(DEFAULT_URL)) {
            this.currentProcessor.set("default");
            log.info("Current processor: {}", this.currentProcessor);
            return DEFAULT_URL;
        }

        if (isServiceHealth(FALLBACK_URL)) {
            this.currentProcessor.set("fallback");
            log.info("Current processor: {}", this.currentProcessor);
            return FALLBACK_URL;
        }

        this.currentProcessor.set("default");
        log.warn("Using default processor as default");
        return DEFAULT_URL;
    }


    private Boolean isServiceHealth(String url){
        try {
            ServiceHealthResponse response = restClient.get()
                    .uri(url + "/payments/service-health")
                    .retrieve()
                    .toEntity(ServiceHealthResponse.class).getBody();

            return response != null && !response.failing();
        } catch (HttpClientErrorException.TooManyRequests e) {
            log.warn("Too many requests for {}: {}", url, e.getResponseBodyAsString());
            return false;
        } catch (Exception e) {
            log.debug("Health check failed for {}: {}", url, e.getMessage());
            return false;
        }
    }

    public String getCurrentProcessor(){
        return this.currentProcessor.get();
    }
}
