package com.victor.rinhabackend2025.entity;

import java.math.BigDecimal;

public class PaymentSummary {

    private Integer totalRequests;
    private BigDecimal totalAmount;

    public Integer getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(Integer totalRequests) {
        this.totalRequests = totalRequests;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
