package com.api_order.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.api_order.dto.CartItemRequest;
import com.api_order.dto.CartItemResponse;
import com.api_order.dto.CartResponse;
import com.api_order.entity.Cart;
import com.api_order.entity.CartItem;
import com.api_order.repository.CartItemRepository;
import com.api_order.repository.CartRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    // Lấy giỏ hàng (tạo mới nếu chưa có)
    @Transactional(readOnly = true)
    public CartResponse getCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElse(Cart.builder().userId(userId).build());
        return toResponse(cart);
    }

    // Thêm sản phẩm vào giỏ hàng
    public CartResponse addItem(String userId, CartItemRequest request) {
        validateCartItemRequest(request);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder().userId(userId).build()));

        cartItemRepository.findByCartAndProductId(cart, request.getProductId())
                .ifPresentOrElse(
                        existing -> existing.setQuantity(existing.getQuantity() + request.getQuantity()),
                        () -> {
                            CartItem newItem = CartItem.builder()
                                    .cart(cart)
                                    .productId(request.getProductId())
                                    .productName(request.getProductName())
                                    .price(request.getPrice())
                                    .quantity(request.getQuantity())
                                    .build();
                            cart.getItems().add(newItem);
                        });

        return toResponse(cartRepository.save(cart));
    }

    // Cập nhật số lượng sản phẩm
    public CartResponse updateItem(String userId, Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số lượng phải lớn hơn 0");
        }

        Cart cart = findCartOrThrow(userId);
        CartItem item = cartItemRepository.findByCartAndProductId(cart, productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Sản phẩm không tồn tại trong giỏ hàng"));

        item.setQuantity(quantity);
        return toResponse(cartRepository.save(cart));
    }

    // Xóa sản phẩm khỏi giỏ hàng
    public CartResponse removeItem(String userId, Long productId) {
        Cart cart = findCartOrThrow(userId);
        CartItem item = cartItemRepository.findByCartAndProductId(cart, productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Sản phẩm không tồn tại trong giỏ hàng"));

        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        return toResponse(cartRepository.save(cart));
    }

    // Xóa toàn bộ giỏ hàng
    public void clearCart(String userId) {
        Cart cart = findCartOrThrow(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private Cart findCartOrThrow(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Giỏ hàng không tồn tại"));
    }

    private void validateCartItemRequest(CartItemRequest request) {
        if (request.getProductId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productId không được để trống");
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số lượng phải lớn hơn 0");
        }
        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giá không hợp lệ");
        }
    }

    private CartItemResponse toItemResponse(CartItem item) {
        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .build();
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        BigDecimal total = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .items(itemResponses)
                .total(total)
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }
}
