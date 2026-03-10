package com.api_report.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

/**
 * Jira-style "Summary" dashboard — key metrics at a glance.
 */
@Data
@Builder
public class DashboardSummaryResponse {

    // ── Orders / Revenue ──────────────────────────────────────────────────────
    private BigDecimal totalRevenue;
    private long totalOrders;
    private long totalItemsSold;

    private long ordersLast7Days;
    private BigDecimal revenueLast7Days;

    // ── Catalog ───────────────────────────────────────────────────────────────
    private long totalProducts;
    private long totalCategories;

    // ── Inventory health ──────────────────────────────────────────────────────
    private long outOfStockCount;
    private long lowStockCount;     // stock > 0 and stock <= 10
}
