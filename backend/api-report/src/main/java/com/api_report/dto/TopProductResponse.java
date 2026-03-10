package com.api_report.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

/**
 * Jira "Issue Statistics" equivalent — top-selling products ranked by units sold.
 */
@Data
@Builder
public class TopProductResponse {

    private int rank;
    private Long productId;
    private String productName;
    private String categoryName;
    private String imageUrl;

    private long totalSold;
    private BigDecimal totalRevenue;
}
