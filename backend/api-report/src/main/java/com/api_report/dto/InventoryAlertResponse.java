package com.api_report.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * Inventory health report — Jira "Backlog" / alert panel equivalent.
 */
@Data
@Builder
public class InventoryAlertResponse {

    /** Stock threshold used for "low stock" classification */
    private int threshold;

    private long outOfStockCount;
    private long lowStockCount;

    private List<InventoryItem> outOfStock;
    private List<InventoryItem> lowStock;
}
