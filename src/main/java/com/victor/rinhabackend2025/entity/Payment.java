package com.victor.rinhabackend2025.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "correlation_id", nullable = false)
    private UUID correlationId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "requested_at", nullable = false)
    private ZonedDateTime requestedAt;

    private String processor;

    public Payment() {
    }

    public Payment(UUID correlationId, BigDecimal amount, ZonedDateTime requestedAt, String processor) {
        this.correlationId = correlationId;
        this.amount = amount;
        this.requestedAt = requestedAt;
        this.processor = processor;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(UUID correlationId) {
        this.correlationId = correlationId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public ZonedDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(ZonedDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }
}
