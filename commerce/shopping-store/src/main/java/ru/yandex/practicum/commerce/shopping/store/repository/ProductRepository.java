package ru.yandex.practicum.commerce.shopping.store.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.commerce.interaction.dto.store.ProductCategory;
import ru.yandex.practicum.commerce.interaction.dto.store.ProductState;
import ru.yandex.practicum.commerce.shopping.store.entity.Product;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Page<Product> findByProductCategoryAndProductState(ProductCategory productCategory, ProductState state, Pageable pageable);
}