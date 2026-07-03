package ru.yandex.practicum.commerce.shopping.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.commerce.shopping.cart.entity.Cart;

import java.util.Optional;
import java.util.UUID;

public interface ShoppingCartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUsername(String username);

    Optional<Cart> findByUsernameAndIsActiveTrue(String username);
}