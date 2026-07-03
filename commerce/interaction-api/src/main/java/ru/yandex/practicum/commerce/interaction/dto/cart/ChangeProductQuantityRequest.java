package ru.yandex.practicum.commerce.interaction.dto.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeProductQuantityRequest {

    @NotNull(message = "Идентификатор товара не может быть пустым")
    private UUID productId;

    @NotNull(message = "Количество товара должно быть указано")
    @PositiveOrZero(message = "Количество товара не может быть отрицательным")
    private Long newQuantity;
}