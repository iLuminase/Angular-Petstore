package com.api_order.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api_order.dto.CartItemRequest;
import com.api_order.dto.CartResponse;
import com.api_order.service.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    private String resolveUserId(Jwt jwt, String userIdHeader) {
        if (jwt != null) return jwt.getSubject();
        if (userIdHeader != null && !userIdHeader.isBlank()) return userIdHeader;
        throw new org.springframework.web.server.ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Thiếu thông tin người dùng");
    }

    // Lấy giỏ hàng của người dùng hiện tại
    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        return ResponseEntity.ok(cartService.getCart(resolveUserId(jwt, userIdHeader)));
    }

    // Thêm sản phẩm vào giỏ hàng
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestBody CartItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartService.addItem(resolveUserId(jwt, userIdHeader), request));
    }

    // Cập nhật số lượng sản phẩm trong giỏ hàng
    @PutMapping("/items/{productId}")
    public ResponseEntity<CartResponse> updateItem(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.updateItem(resolveUserId(jwt, userIdHeader), productId, quantity));
    }

    // Xóa sản phẩm khỏi giỏ hàng
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeItem(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeItem(resolveUserId(jwt, userIdHeader), productId));
    }

    // Xóa toàn bộ giỏ hàng
    @DeleteMapping
    public ResponseEntity<Void> clearCart(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        cartService.clearCart(resolveUserId(jwt, userIdHeader));
        return ResponseEntity.noContent().build();
    }
}
