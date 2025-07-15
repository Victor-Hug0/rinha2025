package com.victor.rinhabackend2025.service;

import com.victor.rinhabackend2025.repository.PaymentRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final HealthCheckService healthCheckService;

    public PaymentService(PaymentRepository paymentRepository, HealthCheckService healthCheckService) {
        this.paymentRepository = paymentRepository;
        this.healthCheckService = healthCheckService;
    }


}
