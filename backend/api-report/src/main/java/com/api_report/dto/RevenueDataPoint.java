package com.api_report.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

/**
 * A single data point used in timeline / bar charts (Jira "Created vs Resolved" equivalent).
 */
@Data
@Builder
public class RevenueDataPoint {

    /** Human-readable label: "2024-01-15" (day) | "2024-W03" (week) | "2024-01" (month) */
    private String label;

    private long orderCount;
    private BigDecimal revenue;
}
