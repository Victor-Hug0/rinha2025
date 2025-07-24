package com.victor.rinhabackend2025.service;

import com.victor.rinhabackend2025.dto.PaymentProcessorRequest;
import com.victor.rinhabackend2025.dto.PaymentRequest;
import com.victor.rinhabackend2025.dto.PaymentSummaryResponse;
import com.victor.rinhabackend2025.dto.ProcessorSummaryDTO;
import com.victor.rinhabackend2025.entity.Payment;
import com.victor.rinhabackend2025.entity.PaymentSummary;
import com.victor.rinhabackend2025.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;
    private final HealthCheckService healthCheckService;
    private final WebClient.Builder webClientBuilder;
    @Value("${default-url}")
    private String DEFAULT_URL;
    @Value("${fallback-url}")
    private String FALLBACK_URL;

    public PaymentService(PaymentRepository paymentRepository, HealthCheckService healthCheckService, WebClient.Builder webClientBuilder) {
        this.paymentRepository = paymentRepository;
        this.healthCheckService = healthCheckService;
        this.webClientBuilder = webClientBuilder;
    }

    public Payment sendToBestProcessor(PaymentRequest paymentRequest) {
        String currentProcessorUrl = healthCheckService.getCurrentProcessorUrl();
        String alternate = (currentProcessorUrl.equals(DEFAULT_URL)) ? FALLBACK_URL : DEFAULT_URL;

        Optional<Payment> result = tryWithRetry(paymentRequest, currentProcessorUrl);

        if (result.isPresent()) {
            String processor = getProcessorName(currentProcessorUrl);
            result.get().setProcessor(processor);
            return result.get();
        }

        Optional<Payment> alternateResult = tryWithRetry(paymentRequest, alternate);
        if (alternateResult.isPresent()) {
            String processor = getProcessorName(currentProcessorUrl);
            alternateResult.get().setProcessor(processor);
            return alternateResult.get();
        }

        return failResult(paymentRequest);
    }


    private Optional<Payment> tryWithRetry(PaymentRequest paymentRequest, String currentProcessorUrl) {
        String processor = (currentProcessorUrl.equals("default")) ? "default" : "fallback";
        WebClient webClient = webClientBuilder.baseUrl(currentProcessorUrl).build();
        for (int i = 1; i <= 3; i++) {
            ZonedDateTime processedAt = ZonedDateTime.now();
            try {

                PaymentProcessorRequest paymentProcessorRequest = new PaymentProcessorRequest(
                        paymentRequest.correlationId(),
                        paymentRequest.amount(),
                        processedAt
                );

                webClient.post()
                        .uri("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(paymentProcessorRequest)
                        .retrieve()
                        .toBodilessEntity()
                        .timeout(Duration.ofMillis(1000))
                        .block();


                return Optional.of(new Payment(
                        paymentRequest.correlationId(),
                        paymentRequest.amount(),
                        processedAt,
                        processor
                ));
            } catch (Exception e) {
                log.error("Error sending payment request", e);

                try {
                    Thread.sleep(50L * i);
                } catch (InterruptedException e1) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        return Optional.empty();
    }

    private Payment failResult(PaymentRequest paymentRequest) {
        return new Payment(
                paymentRequest.correlationId(),
                paymentRequest.amount(),
                null,
                null
        );
    }

    public PaymentSummaryResponse  getPaymentSummary(ZonedDateTime from, ZonedDateTime to) {
        List<ProcessorSummaryDTO> summaryDTOS = (from != null && to != null) ?
                paymentRepository.summarizeByProcessor(from, to) :
                paymentRepository.summarizeByProcessorAll();

        PaymentSummary defaultPaymentSummary = new PaymentSummary(0, BigDecimal.ZERO);
        PaymentSummary fallbackPaymentSummary = new PaymentSummary(0, BigDecimal.ZERO);

        for (ProcessorSummaryDTO summaryDTO : summaryDTOS) {
            if (summaryDTO.processor().equals("default")) {
                defaultPaymentSummary = new PaymentSummary(summaryDTO.totalRequests().intValue(), summaryDTO.totalAmount());
            } else if (summaryDTO.processor().equals("fallback")) {
                fallbackPaymentSummary = new PaymentSummary(summaryDTO.totalRequests().intValue(), summaryDTO.totalAmount());
            }
        }

        log.info("Default payment summary: {}", defaultPaymentSummary);
        log.info("Fallback payment summary: {}", fallbackPaymentSummary);
        return new PaymentSummaryResponse(defaultPaymentSummary, fallbackPaymentSummary);
    }


    private String getProcessorName(String url) {
        if (DEFAULT_URL.equals(url)) return "default";
        if (FALLBACK_URL.equals(url)) return "fallback";
        return "unknown";
    }
}
