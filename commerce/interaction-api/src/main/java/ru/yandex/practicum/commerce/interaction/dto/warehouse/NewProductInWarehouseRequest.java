package ru.yandex.practicum.commerce.interaction.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewProductInWarehouseRequest {

    @NotNull(message = "Идентификатор товара должен быть указан")
    private UUID productId;

    private boolean fragile;

    @NotNull(message = "Размеры товара должны быть указаны")
    private DimensionDto dimension;

    @NotNull(message = "Вес товара должен быть указан")
    private Double weight;
}