package com.api_report.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

/**
 * Latest orders summary — Jira "Recently Created Issues" equivalent.
 */
@Data
@Builder
public class RecentOrderResponse {

    private Long orderId;
    private String userId;
    private long itemCount;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
}
