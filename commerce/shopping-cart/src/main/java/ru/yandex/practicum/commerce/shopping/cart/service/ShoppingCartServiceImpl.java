package ru.yandex.practicum.commerce.shopping.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.interaction.client.WarehouseClient;
import ru.yandex.practicum.commerce.interaction.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.interaction.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.interaction.exception.CartNotFoundException;
import ru.yandex.practicum.commerce.interaction.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.commerce.interaction.exception.NotAuthorizedUserException;
import ru.yandex.practicum.commerce.shopping.cart.entity.Cart;
import ru.yandex.practicum.commerce.shopping.cart.mapper.ShoppingCartMapper;
import ru.yandex.practicum.commerce.shopping.cart.repository.ShoppingCartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository cartRepository;
    private final WarehouseClient warehouseClient;
    private final ShoppingCartMapper cartMapper;

    @Override
    @Transactional(readOnly = true)
    public ShoppingCartDto getShoppingCart(String username) {
        checkUsername(username);
        Cart cart = getOrCreateActiveCart(username);
        return cartMapper.toCartDto(cart);
    }

    @Override
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Integer> products) {
        checkUsername(username);
        Cart cart = getOrCreateActiveCart(username);

        products.forEach((productId, quantity) ->
                cart.getProducts().merge(productId, quantity, Integer::sum));

        ShoppingCartDto cartDto = cartMapper.toCartDto(cart);
        warehouseClient.checkProductQuantityEnoughForShoppingCart(cartDto);
        return cartMapper.toCartDto(cartRepository.save(cart));
    }

    @Override
    public void deactivateCurrentShoppingCart(String username) {
        checkUsername(username);
        Cart cart = getActiveCartOrThrow(username);

        cart.setActive(false);
        cartRepository.save(cart);
    }

    @Override
    public ShoppingCartDto removeFromShoppingCart(String username, List<UUID> productIds) {
        checkUsername(username);
        Cart cart = getActiveCartOrThrow(username);
        if (cart.getProducts().isEmpty()) {
            throw new NoProductsInShoppingCartException("Корзина пустая. Удаление невозможна",
                    "Корзина пустая. Удаление невозможна");
        }

        productIds.forEach(productId -> cart.getProducts().remove(productId));
        return cartMapper.toCartDto(cartRepository.save(cart));
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest changeRequest) {
        checkUsername(username);
        Cart cart = getActiveCartOrThrow(username);
        if (!cart.getProducts().containsKey(changeRequest.getProductId())) {
            throw new NoProductsInShoppingCartException("Товар не найден в корзине",
                    "Нет искомых товаров в корзине");
        }

        cart.getProducts().put(changeRequest.getProductId(), changeRequest.getNewQuantity());
        ShoppingCartDto cartDto = cartMapper.toCartDto(cart);
        warehouseClient.checkProductQuantityEnoughForShoppingCart(cartDto);
        return cartMapper.toCartDto(cartRepository.save(cart));
    }

    private Cart getOrCreateActiveCart(String username) {
        checkUsername(username);
        return cartRepository.findByUsernameAndIsActiveTrue(username)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUsername(username);
                    cart.setActive(true);
                    cart.setProducts(new HashMap<>());
                    return cartRepository.save(cart);
                });
    }

    private Cart getActiveCartOrThrow(String username) {
        return cartRepository.findByUsernameAndIsActiveTrue(username).orElseThrow(() ->
                new CartNotFoundException("Активная корзина для пользователя не найдена: " + username,
                        "Активная корзина для пользователя не найдена: " + username));
    }

    private void checkUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не может быть пустым", "Указан пустое имя пользователя");
        }
    }
}