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
    private final AtomicReference<String> currentProcessorUrl = new AtomicReference<>(DEFAULT_URL);
    private final RestClient restClient;

    public HealthCheckService(RestClient restClient) {
        this.restClient = restClient;
    }

    @Scheduled(fixedRate = 5000)
    public void chooseProcessor(){
        if (isServiceHealth(DEFAULT_URL)) {
            currentProcessorUrl.set(DEFAULT_URL);
            log.info("Current processor: DEFAULT_URL ({})", DEFAULT_URL);
            return;
        }

        if (isServiceHealth(FALLBACK_URL)) {
            currentProcessorUrl.set(FALLBACK_URL);
            log.info("Current processor: FALLBACK_URL ({})", FALLBACK_URL);
            return;
        }

        currentProcessorUrl.set(DEFAULT_URL);
        log.warn("Both processors unavailable, using DEFAULT_URL as fallback ({})", DEFAULT_URL);
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

    public String getCurrentProcessorUrl(){
        return this.currentProcessorUrl.get();
    }
}
