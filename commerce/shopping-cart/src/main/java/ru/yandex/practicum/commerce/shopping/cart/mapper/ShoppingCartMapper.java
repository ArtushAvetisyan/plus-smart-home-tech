package ru.yandex.practicum.commerce.shopping.cart.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.yandex.practicum.commerce.interaction.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.shopping.cart.entity.Cart;
import ru.yandex.practicum.commerce.shopping.cart.entity.CartItem;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {

    @Mapping(source = "items", target = "products", qualifiedByName = "mapItemsToProducts")
    ShoppingCartDto toCartDto(Cart cart);

    @Named("mapItemsToProducts")
    default Map<UUID, Integer> mapItemsToProducts(List<CartItem> items) {
        if (items == null) return Collections.emptyMap();
        return items.stream()
                .collect(Collectors.toMap(
                        CartItem::getProductId,
                        CartItem::getQuantity
                ));
    }
}