package com.api_product.service;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.api_product.dto.CategoryRequest;
import com.api_product.dto.CategoryResponse;
import com.api_product.entity.Category;
import com.api_product.repository.CategoryRepository;
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream().map(this::toResponse).toList();
    }

    public CategoryResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên loại đã tồn tại. Vui lòng đặt tên khác.");
        }
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return toResponse(categoryRepository.save(category));
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findOrThrow(id);
        if (!category.getName().equals(request.getName()) && categoryRepository.existsByName(request.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên loại đã tồn tại. Vui lòng đặt tên khác.");
        }
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return toResponse(categoryRepository.save(category));
    }

    public void delete(Long id) {
        Category category = findOrThrow(id);
        categoryRepository.delete(category);
    }
    private Category findOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy loại với ID: " + id));
    }
    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

}
