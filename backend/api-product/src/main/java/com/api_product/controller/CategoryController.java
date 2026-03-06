package com.api_product.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_product.dto.CategoryRequest;
import com.api_product.dto.CategoryResponse;
import com.api_product.dto.ProductResponse;
import com.api_product.service.CategoryService;
import com.api_product.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor

public class CategoryController {
    private final CategoryService categoryService;
    private final ProductService productService;
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    // Tim theo id
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }
    // Tim san pham theo id danh muc
    @GetMapping("/{id}/products")
    public ResponseEntity<List<ProductResponse>> getProductsByCategoryId(@PathVariable Long id, Pageable pageable) 
    {
        categoryService.getById(id); // Kiểm tra xem danh mục có tồn tại không
        return ResponseEntity.ok(productService.getByCategory(id, pageable).getContent());
    }
    // Tạo mới
    @PostMapping
    public ResponseEntity<CategoryResponse> create(@RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(request));
    }

    // Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable Long id, @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.update(id, request));
    }
    // Xóa
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
