package com.victor.rinhabackend2025.service;

import com.victor.rinhabackend2025.dto.PaymentProcessorRequest;
import com.victor.rinhabackend2025.dto.PaymentRequest;
import com.victor.rinhabackend2025.entity.Payment;
import com.victor.rinhabackend2025.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;
    private final HealthCheckService healthCheckService;
    private final RestClient restClient;
    @Value("${default-url}")
    private String DEFAULT_URL;

    public PaymentService(PaymentRepository paymentRepository, HealthCheckService healthCheckService, RestClient restClient) {
        this.paymentRepository = paymentRepository;
        this.healthCheckService = healthCheckService;
        this.restClient = restClient;
    }


    public void processPayment(PaymentRequest paymentRequest) {
        Payment payment = new Payment();
        payment.setCorrelationId(paymentRequest.correlationId());
        payment.setAmount(paymentRequest.amount());

        String currentProcessorUrl = healthCheckService.getCurrentProcessorUrl();
        String processor;

        if (currentProcessorUrl.equals(DEFAULT_URL)){
            processor = "default";
        } else {
            processor = "fallback";
        }

        payment.setProcessor(processor);
        payment.setRequestedAt(Instant.now());
        sendPaymentRequest(payment, healthCheckService.getCurrentProcessorUrl());
        paymentRepository.save(payment);
    }

    private void sendPaymentRequest(Payment payment, String processorUrl) {
        try {
            PaymentProcessorRequest paymentProcessorRequest = new PaymentProcessorRequest(payment.getCorrelationId(), payment.getAmount(), payment.getRequestedAt());

            restClient.post()
                    .uri(processorUrl + "/payments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(paymentProcessorRequest)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Error sending payment request", e);
        }
    }
}
