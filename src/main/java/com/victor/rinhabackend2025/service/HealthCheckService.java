package com.victor.rinhabackend2025.service;

import com.victor.rinhabackend2025.dto.ServiceHealthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class HealthCheckService {

    private static final Logger log = LoggerFactory.getLogger(HealthCheckService.class);
    @Value("${default-url}")
    private String DEFAULT_URL;
    @Value("${fallback-url}")
    private String FALLBACK_URL;
    private final AtomicReference<String> currentProcessorUrl = new AtomicReference<>(DEFAULT_URL);
    private final WebClient.Builder webClientBuilder;

    public HealthCheckService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Scheduled(fixedRate = 5000)
    public void updateHealthCheck() {
        Boolean defaultHealth = isProcessorHealthy(DEFAULT_URL);
        Boolean fallbackHealth = isProcessorHealthy(FALLBACK_URL);

        currentProcessorUrl.set(chooseProcessor(defaultHealth, fallbackHealth));
    }

    private String chooseProcessor(boolean defaultHealth, boolean fallbackHealth) {
        if (defaultHealth) {
            return DEFAULT_URL;
        } else if (fallbackHealth) {
            return FALLBACK_URL;
        } else {
            return DEFAULT_URL;
        }
    }

    private Boolean isProcessorHealthy(String processorUrl) {
        Optional<ServiceHealthResponse> response = getHealth(processorUrl);
        return response.isPresent() && response.get().failing();
    }

    private Optional<ServiceHealthResponse> getHealth(String url){
        WebClient webClient = webClientBuilder.baseUrl(url).build();
        try {
            ServiceHealthResponse serviceHealthResponse = webClient.get()
                    .uri("/payments/service-health")
                    .retrieve()
                    .bodyToMono(ServiceHealthResponse.class)
                    .timeout(Duration.ofMillis(250))
                    .onErrorResume(e -> {
                        log.warn("Service health check failed", e);
                        return Mono.empty();
                    })
                    .blockOptional()
                    .orElse(null);

            return Optional.ofNullable(serviceHealthResponse);

        } catch (Exception e){
            log.warn("Service health check failed", e);
            return Optional.empty();
        }
    }

    public String getCurrentProcessorUrl(){
        return this.currentProcessorUrl.get();
    }
}
