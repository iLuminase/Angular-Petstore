package com.api_product.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.api_product.dto.ProductRequest;
import com.api_product.dto.ProductResponse;
import com.api_product.entity.Category;
import com.api_product.entity.Product;
import com.api_product.repository.CategoryRepository;
import com.api_product.repository.ProductRepository;
import com.github.slugify.Slugify;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final Slugify slugify = Slugify.builder().transliterator(true).underscoreSeparator(false).build();  //chuyen tieng viet sang tieng latin

    public Page<ProductResponse> getAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::toResponse);
    }

    public Page<ProductResponse> search(String keyword, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(keyword, pageable).map(this::toResponse);
    }

    public Page<ProductResponse> getByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategory_Id(categoryId, pageable).map(this::toResponse);
    }

    public ProductResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsByName(request.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sản phẩm với tên này đã tồn tại");
        }
        Category category = findCategoryOrThrow(request.getCategoryId());
        
        Product product = Product.builder()
                .name(request.getName())
                .slug(slugify.slugify(request.getName()))
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .category(category)
                .imageUrl(request.getImageUrl())
                .build();
        return toResponse(productRepository.save(product));
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findOrThrow(id);
        Category category = findCategoryOrThrow(request.getCategoryId());
        if (productRepository.existsByNameAndIdNot(request.getName(), id)) {
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên sản phẩm đã tồn tại.");
}
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(category);
        product.setImageUrl(request.getImageUrl());
        product.setSlug(slugify.slugify(request.getName()));
        return toResponse(productRepository.save(product));
    }

    public void delete(Long id) {
        findOrThrow(id);
        productRepository.deleteById(id);
    }

    private Product findOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy sản phẩm với id: " + id));
    }

    private Category findCategoryOrThrow(Long categoryId) {
        if (categoryId == null) return null;
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy danh mục với id: " + categoryId));
    }

    private ProductResponse toResponse(Product product) {
        Long categoryId = product.getCategory() != null ? product.getCategory().getId() : null;
        String categoryName = product.getCategory() != null ? product.getCategory().getName() : null;
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .categoryId(categoryId)
                .categoryName(categoryName)
                .imageUrl(product.getImageUrl())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}