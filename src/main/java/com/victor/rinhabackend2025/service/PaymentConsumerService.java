package com.victor.rinhabackend2025.service;

import com.victor.rinhabackend2025.dto.PaymentRequest;
import com.victor.rinhabackend2025.entity.Payment;
import com.victor.rinhabackend2025.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentConsumerService {

    private static final Logger log = LoggerFactory.getLogger(PaymentConsumerService.class);
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    public PaymentConsumerService(PaymentService paymentService,  PaymentRepository paymentRepository) {
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
    }

    public void handleMessage(PaymentRequest paymentRequest) {

        try {
            if (paymentRepository.existsByCorrelationId(paymentRequest.correlationId())){
                log.warn("Payment already exists for correlationId {}", paymentRequest.correlationId());
                return;
            }

            Payment payment = paymentService.sendToBestProcessor(paymentRequest);
            paymentRepository.save(payment);

            log.info("Payment processed and save with correlationId {}", paymentRequest.correlationId());
        } catch (Exception e){
            log.error("Error while sending Payment to Best Processor", e);
            throw new RuntimeException("Error while sending Payment to Best Processor", e);
        }


    }
}
