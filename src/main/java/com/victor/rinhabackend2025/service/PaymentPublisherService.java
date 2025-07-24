package com.victor.rinhabackend2025.service;

import com.victor.rinhabackend2025.dto.PaymentRequest;
import com.victor.rinhabackend2025.entity.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentPublisherService {

    private static final Logger log = LoggerFactory.getLogger(PaymentPublisherService.class);
    private static final String CHANNEL_NAME = "payments";
    private final RedisTemplate<String, Object>  redisTemplate;

    public PaymentPublisherService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void publishPayment(PaymentRequest paymentRequest) {
        try {
            redisTemplate.opsForList().rightPush(CHANNEL_NAME, paymentRequest);
            log.info("Message sent to payment publisher: {}", paymentRequest.toString());
        }  catch (Exception ex) {
            log.error("Error while sending payment request to payment publisher", ex);
        }
    }
}
