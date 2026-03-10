package com.api_report.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

/**
 * Revenue performance per category — Jira "Issue Statistics by Component" equivalent.
 */
@Data
@Builder
public class CategorySalesResponse {

    private Long categoryId;
    private String categoryName;

    private long totalSold;
    private BigDecimal totalRevenue;
    private long productCount;

    /** Share of total revenue (0 – 100) */
    private double percentageOfTotal;
}
