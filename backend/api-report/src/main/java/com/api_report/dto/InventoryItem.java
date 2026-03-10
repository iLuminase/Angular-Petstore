package com.api_report.dto;

import lombok.Builder;
import lombok.Data;

/**
 * A single product with a stock alert (out-of-stock or low-stock).
 */
@Data
@Builder
public class InventoryItem {

    private Long productId;
    private String productName;
    private String categoryName;
    private int currentStock;

    /** "OUT_OF_STOCK" or "LOW_STOCK" */
    private String status;
}
