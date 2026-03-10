package com.api_report.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api_report.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // ── Jira "Issue Statistics" equivalents ───────────────────────────────────

    /**
     * Top-selling products. Joins into petstore_store via MySQL cross-DB reference.
     */
    @Query(value = """
            SELECT ci.product_id                             AS productId,
                   ci.product_name                           AS productName,
                   SUM(ci.quantity)                          AS totalQuantity,
                   SUM(ci.price * ci.quantity)               AS totalRevenue,
                   p.image_url                               AS imageUrl,
                   p.category_id                             AS categoryId,
                   cat.name                                  AS categoryName
            FROM   cart_items ci
            LEFT JOIN petstore_store.products   p   ON ci.product_id   = p.id
            LEFT JOIN petstore_store.categories cat ON p.category_id   = cat.id
            GROUP  BY ci.product_id, ci.product_name, p.image_url, p.category_id, cat.name
            ORDER  BY totalQuantity DESC
            LIMIT  :limit
            """, nativeQuery = true)
    List<Object[]> findTopProducts(@Param("limit") int limit);

    /**
     * Revenue and quantity sold broken down by category.
     */
    @Query(value = """
            SELECT cat.id                                    AS categoryId,
                   cat.name                                  AS categoryName,
                   SUM(ci.quantity)                          AS totalQuantity,
                   SUM(ci.price * ci.quantity)               AS totalRevenue,
                   COUNT(DISTINCT ci.product_id)             AS productCount
            FROM   cart_items ci
            LEFT JOIN petstore_store.products   p   ON ci.product_id   = p.id
            LEFT JOIN petstore_store.categories cat ON p.category_id   = cat.id
            GROUP  BY cat.id, cat.name
            ORDER  BY totalRevenue DESC
            """, nativeQuery = true)
    List<Object[]> findCategorySales();

    // ── Aggregate helpers ─────────────────────────────────────────────────────

    @Query("SELECT COALESCE(SUM(ci.price * ci.quantity), 0) FROM CartItem ci")
    BigDecimal findTotalRevenue();

    @Query("SELECT COALESCE(SUM(ci.quantity), 0) FROM CartItem ci")
    Long findTotalItemsSold();
}
