package com.api_report.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * Revenue/Order trend over time — equivalent to Jira's "Created vs Resolved" line chart.
 */
@Data
@Builder
public class RevenueTimelineResponse {

    private String from;
    private String to;

    /** "day" | "week" | "month" */
    private String groupBy;

    private List<RevenueDataPoint> dataPoints;

    private BigDecimal totalRevenue;
    private long totalOrders;
}
