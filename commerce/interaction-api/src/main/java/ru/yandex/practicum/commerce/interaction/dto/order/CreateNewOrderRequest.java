package ru.yandex.practicum.commerce.interaction.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.commerce.interaction.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.interaction.dto.warehouse.AddressDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNewOrderRequest {

    @NotNull(message = "Данные корзины обязательны для создания заказа")
    private ShoppingCartDto shoppingCart;

    @NotNull(message = "Адрес доставки обязателен")
    private AddressDto deliveryAddress;
}