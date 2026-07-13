package ru.yandex.practicum.commerce.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.commerce.interaction.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.interaction.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interaction.dto.order.OrderState;
import ru.yandex.practicum.commerce.order.entity.Order;

import java.util.Map;
import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {UUID.class, OrderState.class})
public interface OrderMapper {

    @Mapping(target = "orderId", expression = "java(UUID.randomUUID())")
    @Mapping(target = "shoppingCartId", source = "cart.shoppingCartId")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "products", source = "cartProducts")
    @Mapping(target = "state", expression = "java(OrderState.NEW)")
    Order toOrder(String username, ShoppingCartDto cart, Map<UUID, Integer> cartProducts);

    OrderDto toOrderDto(Order order);
}