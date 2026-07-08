package ru.yandex.practicum.commerce.shopping.cart.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.commerce.interaction.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.shopping.cart.entity.Cart;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {

    ShoppingCartDto toCartDto(Cart cart);
}