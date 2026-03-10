package com.api_report.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api_report.dto.CategorySalesResponse;
import com.api_report.dto.DashboardSummaryResponse;
import com.api_report.dto.InventoryAlertResponse;
import com.api_report.dto.RecentOrderResponse;
import com.api_report.dto.RevenueTimelineResponse;
import com.api_report.dto.TopProductResponse;
import com.api_report.service.ReportService;

import lombok.RequiredArgsConstructor;

/**
 * Report API — inspired by Jira's reporting panel.
 *
 * Endpoints:
 *   GET /api/reports/dashboard           → Summary (Jira "Summary page")
 *   GET /api/reports/revenue             → Revenue/Order timeline (Jira "Created vs Resolved")
 *   GET /api/reports/products/top        → Top-selling products  (Jira "Issue Statistics")
 *   GET /api/reports/categories/sales    → Revenue by category   (Jira "Issue Statistics by Component")
 *   GET /api/reports/inventory/alerts    → Stock health          (Jira "Backlog alerts")
 *   GET /api/reports/orders/recent       → Latest orders         (Jira "Recently Created Issues")
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // ── Dashboard ─────────────────────────────────────────────────────────────

    /**
     * Overall KPI summary — total revenue, orders, product catalog health.
     *
     * GET /api/reports/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardSummaryResponse> getDashboard() {
        return ResponseEntity.ok(reportService.getDashboardSummary());
    }

    // ── Revenue Timeline ──────────────────────────────────────────────────────

    /**
     * Order/revenue trend over a date range, grouped by day / week / month.
     *
     * GET /api/reports/revenue?from=2024-01-01&to=2024-12-31&groupBy=month
     *
     * @param from    Start date (ISO, inclusive). Defaults to 30 days ago.
     * @param to      End date   (ISO, inclusive). Defaults to today.
     * @param groupBy "day" | "week" | "month" (default: "day")
     */
    @GetMapping("/revenue")
    public ResponseEntity<RevenueTimelineResponse> getRevenueTimeline(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "day") String groupBy) {

        LocalDate toDate   = to   != null ? to   : LocalDate.now();
        LocalDate fromDate = from != null ? from : toDate.minusDays(30);

        return ResponseEntity.ok(reportService.getRevenueTimeline(fromDate, toDate, groupBy));
    }

    // ── Top Products ──────────────────────────────────────────────────────────

    /**
     * Products ranked by units sold — Jira "Issue Statistics" equivalent.
     *
     * GET /api/reports/products/top?limit=10
     *
     * @param limit Maximum number of products to return (1–100, default 10)
     */
    @GetMapping("/products/top")
    public ResponseEntity<List<TopProductResponse>> getTopProducts(
            @RequestParam(defaultValue = "10") int limit) {

        int safeLimit = Math.max(1, Math.min(limit, 100));
        return ResponseEntity.ok(reportService.getTopProducts(safeLimit));
    }

    // ── Category Sales ────────────────────────────────────────────────────────

    /**
     * Revenue and units sold broken down by category.
     *
     * GET /api/reports/categories/sales
     */
    @GetMapping("/categories/sales")
    public ResponseEntity<List<CategorySalesResponse>> getCategorySales() {
        return ResponseEntity.ok(reportService.getCategorySales());
    }

    // ── Inventory Alerts ──────────────────────────────────────────────────────

    /**
     * Products that are out-of-stock or below the given threshold.
     *
     * GET /api/reports/inventory/alerts?threshold=10
     *
     * @param threshold Max stock count to classify a product as "low stock" (default 10)
     */
    @GetMapping("/inventory/alerts")
    public ResponseEntity<InventoryAlertResponse> getInventoryAlerts(
            @RequestParam(defaultValue = "10") int threshold) {

        int safeThreshold = Math.max(1, Math.min(threshold, 1000));
        return ResponseEntity.ok(reportService.getInventoryAlerts(safeThreshold));
    }

    // ── Recent Orders ─────────────────────────────────────────────────────────

    /**
     * The N most recently created orders — Jira "Recently Created Issues" equivalent.
     *
     * GET /api/reports/orders/recent?limit=10
     *
     * @param limit Number of orders to return (1–100, default 10)
     */
    @GetMapping("/orders/recent")
    public ResponseEntity<List<RecentOrderResponse>> getRecentOrders(
            @RequestParam(defaultValue = "10") int limit) {

        int safeLimit = Math.max(1, Math.min(limit, 100));
        return ResponseEntity.ok(reportService.getRecentOrders(safeLimit));
    }
}
