package com.api_report.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api_report.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    long countByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    // ── Timeline queries (Jira-style "Created vs Resolved" chart) ─────────────

    @Query(value = """
            SELECT DATE(c.created_at)                            AS label,
                   COUNT(c.id)                                   AS orderCount,
                   COALESCE(SUM(ci.price * ci.quantity), 0)      AS revenue
            FROM   carts c
            LEFT JOIN cart_items ci ON ci.cart_id = c.id
            WHERE  c.created_at BETWEEN :from AND :to
            GROUP  BY DATE(c.created_at)
            ORDER  BY DATE(c.created_at)
            """, nativeQuery = true)
    List<Object[]> findDailyTimeline(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query(value = """
            SELECT CONCAT(YEAR(c.created_at), '-W', LPAD(WEEK(c.created_at, 1), 2, '0')) AS label,
                   COUNT(c.id)                                                             AS orderCount,
                   COALESCE(SUM(ci.price * ci.quantity), 0)                               AS revenue
            FROM   carts c
            LEFT JOIN cart_items ci ON ci.cart_id = c.id
            WHERE  c.created_at BETWEEN :from AND :to
            GROUP  BY YEARWEEK(c.created_at, 1)
            ORDER  BY YEARWEEK(c.created_at, 1)
            """, nativeQuery = true)
    List<Object[]> findWeeklyTimeline(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query(value = """
            SELECT DATE_FORMAT(c.created_at, '%Y-%m')        AS label,
                   COUNT(c.id)                               AS orderCount,
                   COALESCE(SUM(ci.price * ci.quantity), 0)  AS revenue
            FROM   carts c
            LEFT JOIN cart_items ci ON ci.cart_id = c.id
            WHERE  c.created_at BETWEEN :from AND :to
            GROUP  BY DATE_FORMAT(c.created_at, '%Y-%m')
            ORDER  BY DATE_FORMAT(c.created_at, '%Y-%m')
            """, nativeQuery = true)
    List<Object[]> findMonthlyTimeline(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    // ── Recent orders ─────────────────────────────────────────────────────────

    @Query(value = """
            SELECT c.id, c.user_id, c.created_at, c.updated_at,
                   COUNT(ci.id)                              AS itemCount,
                   COALESCE(SUM(ci.price * ci.quantity), 0)  AS totalAmount
            FROM   carts c
            LEFT JOIN cart_items ci ON ci.cart_id = c.id
            GROUP  BY c.id, c.user_id, c.created_at, c.updated_at
            ORDER  BY c.created_at DESC
            LIMIT  :limit
            """, nativeQuery = true)
    List<Object[]> findRecentOrders(@Param("limit") int limit);
}
