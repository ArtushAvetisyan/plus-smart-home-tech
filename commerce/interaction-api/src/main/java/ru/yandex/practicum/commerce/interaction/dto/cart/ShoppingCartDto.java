package ru.yandex.practicum.commerce.interaction.dto.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartDto {

    @NotNull(message = "Идентификатор корзины не может быть пустым")
    private UUID shoppingCartId;

    private Map<UUID, @NotNull @Positive Integer> products;
}