package com.api_report.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_report.dto.CategorySalesResponse;
import com.api_report.dto.DashboardSummaryResponse;
import com.api_report.dto.InventoryAlertResponse;
import com.api_report.dto.InventoryItem;
import com.api_report.dto.RecentOrderResponse;
import com.api_report.dto.RevenueDataPoint;
import com.api_report.dto.RevenueTimelineResponse;
import com.api_report.dto.TopProductResponse;
import com.api_report.repository.CartItemRepository;
import com.api_report.repository.CartRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final JdbcTemplate jdbcTemplate;

    // ── Dashboard Summary ─────────────────────────────────────────────────────

    /**
     * Jira "Summary" page equivalent — overall health metrics.
     */
    public DashboardSummaryResponse getDashboardSummary() {

        long totalOrders   = cartRepository.count();
        BigDecimal totalRevenue   = cartItemRepository.findTotalRevenue();
        Long totalItemsSold = cartItemRepository.findTotalItemsSold();

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        long ordersLast7Days = cartRepository.countByCreatedAtBetween(sevenDaysAgo, LocalDateTime.now());

        // Cross-database queries → petstore_store
        Long totalProducts = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM petstore_store.products", Long.class);
        Long totalCategories = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM petstore_store.categories", Long.class);
        Long outOfStockCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM petstore_store.products WHERE stock = 0", Long.class);
        Long lowStockCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM petstore_store.products WHERE stock > 0 AND stock <= 10", Long.class);

        BigDecimal revenueLast7Days = jdbcTemplate.queryForObject(
                """
                SELECT COALESCE(SUM(ci.price * ci.quantity), 0)
                FROM   petstore_system.cart_items ci
                JOIN   petstore_system.carts c ON ci.cart_id = c.id
                WHERE  c.created_at >= ?
                """,
                BigDecimal.class,
                sevenDaysAgo);

        return DashboardSummaryResponse.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .totalItemsSold(totalItemsSold != null ? totalItemsSold : 0L)
                .ordersLast7Days(ordersLast7Days)
                .revenueLast7Days(revenueLast7Days != null ? revenueLast7Days : BigDecimal.ZERO)
                .totalProducts(totalProducts != null ? totalProducts : 0L)
                .totalCategories(totalCategories != null ? totalCategories : 0L)
                .outOfStockCount(outOfStockCount != null ? outOfStockCount : 0L)
                .lowStockCount(lowStockCount != null ? lowStockCount : 0L)
                .build();
    }

    // ── Revenue Timeline ──────────────────────────────────────────────────────

    /**
     * Jira "Created vs Resolved" line-chart equivalent.
     * groupBy: "day" (default) | "week" | "month"
     */
    public RevenueTimelineResponse getRevenueTimeline(LocalDate from, LocalDate to, String groupBy) {

        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt   = to.atTime(23, 59, 59);

        List<Object[]> raw = switch (groupBy.toLowerCase()) {
            case "week"  -> cartRepository.findWeeklyTimeline(fromDt, toDt);
            case "month" -> cartRepository.findMonthlyTimeline(fromDt, toDt);
            default      -> cartRepository.findDailyTimeline(fromDt, toDt);
        };

        List<RevenueDataPoint> dataPoints = raw.stream()
                .map(row -> RevenueDataPoint.builder()
                        .label(row[0].toString())
                        .orderCount(((Number) row[1]).longValue())
                        .revenue(new BigDecimal(row[2].toString()))
                        .build())
                .toList();

        BigDecimal totalRevenue = dataPoints.stream()
                .map(RevenueDataPoint::getRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalOrders = dataPoints.stream()
                .mapToLong(RevenueDataPoint::getOrderCount)
                .sum();

        return RevenueTimelineResponse.builder()
                .from(from.toString())
                .to(to.toString())
                .groupBy(groupBy.toLowerCase())
                .dataPoints(dataPoints)
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .build();
    }

    // ── Top Products ──────────────────────────────────────────────────────────

    /**
     * Jira "Issue Statistics" equivalent — top N products ranked by units sold.
     */
    public List<TopProductResponse> getTopProducts(int limit) {

        List<Object[]> raw = cartItemRepository.findTopProducts(limit);
        AtomicInteger rank = new AtomicInteger(1);

        return raw.stream()
                .map(row -> TopProductResponse.builder()
                        .rank(rank.getAndIncrement())
                        .productId(((Number) row[0]).longValue())
                        .productName((String) row[1])
                        .totalSold(((Number) row[2]).longValue())
                        .totalRevenue(new BigDecimal(row[3].toString()))
                        .imageUrl(row[4] != null ? (String) row[4] : null)
                        .categoryName(row[6] != null ? (String) row[6] : "Uncategorized")
                        .build())
                .toList();
    }

    // ── Category Sales ────────────────────────────────────────────────────────

    /**
     * Jira "Issue Statistics by Component" equivalent.
     */
    public List<CategorySalesResponse> getCategorySales() {

        List<Object[]> raw = cartItemRepository.findCategorySales();

        BigDecimal grandTotal = raw.stream()
                .map(row -> new BigDecimal(row[3].toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return raw.stream()
                .map(row -> {
                    BigDecimal revenue = new BigDecimal(row[3].toString());
                    double pct = grandTotal.compareTo(BigDecimal.ZERO) == 0 ? 0.0
                            : revenue.divide(grandTotal, 6, RoundingMode.HALF_UP)
                                     .multiply(BigDecimal.valueOf(100))
                                     .doubleValue();

                    return CategorySalesResponse.builder()
                            .categoryId(row[0] != null ? ((Number) row[0]).longValue() : null)
                            .categoryName(row[1] != null ? (String) row[1] : "Uncategorized")
                            .totalSold(((Number) row[2]).longValue())
                            .totalRevenue(revenue)
                            .productCount(((Number) row[4]).longValue())
                            .percentageOfTotal(Math.round(pct * 100.0) / 100.0)
                            .build();
                })
                .toList();
    }

    // ── Inventory Alerts ──────────────────────────────────────────────────────

    /**
     * Jira "Backlog" alert panel equivalent — highlights products needing restocking.
     */
    public InventoryAlertResponse getInventoryAlerts(int threshold) {

        List<InventoryItem> alerts = jdbcTemplate.query(
                """
                SELECT p.id, p.name, p.stock, c.name AS categoryName
                FROM   petstore_store.products   p
                LEFT JOIN petstore_store.categories c ON p.category_id = c.id
                WHERE  p.stock <= ?
                ORDER  BY p.stock ASC
                """,
                (rs, rowNum) -> InventoryItem.builder()
                        .productId(rs.getLong("id"))
                        .productName(rs.getString("name"))
                        .categoryName(rs.getString("categoryName"))
                        .currentStock(rs.getInt("stock"))
                        .status(rs.getInt("stock") == 0 ? "OUT_OF_STOCK" : "LOW_STOCK")
                        .build(),
                threshold);

        List<InventoryItem> outOfStock = alerts.stream().filter(i -> i.getCurrentStock() == 0).toList();
        List<InventoryItem> lowStock   = alerts.stream().filter(i -> i.getCurrentStock() > 0).toList();

        return InventoryAlertResponse.builder()
                .threshold(threshold)
                .outOfStockCount(outOfStock.size())
                .lowStockCount(lowStock.size())
                .outOfStock(outOfStock)
                .lowStock(lowStock)
                .build();
    }

    // ── Recent Orders ─────────────────────────────────────────────────────────

    /**
     * Jira "Recently Created Issues" equivalent.
     */
    public List<RecentOrderResponse> getRecentOrders(int limit) {

        return cartRepository.findRecentOrders(limit).stream()
                .map(row -> RecentOrderResponse.builder()
                        .orderId(((Number) row[0]).longValue())
                        .userId((String) row[1])
                        .createdAt(((java.sql.Timestamp) row[2]).toLocalDateTime())
                        .itemCount(((Number) row[4]).longValue())
                        .totalAmount(new BigDecimal(row[5].toString()))
                        .build())
                .toList();
    }
}
