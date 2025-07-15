package com.victor.rinhabackend2025.dto;

public record ServiceHealthResponse(
        Boolean failing,
        Integer minResponseTime
) {
}
