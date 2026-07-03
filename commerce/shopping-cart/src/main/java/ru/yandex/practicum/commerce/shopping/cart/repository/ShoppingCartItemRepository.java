package ru.yandex.practicum.commerce.shopping.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.commerce.shopping.cart.entity.CartItem;

import java.util.Optional;
import java.util.UUID;

public interface ShoppingCartItemRepository extends JpaRepository<CartItem, UUID> {
    Optional<CartItem> findByCart_CartIdAndProductId(UUID cartId, UUID productId);
}