package com.api_product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api_product.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByCategory_Id(Long categoryId, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
}
