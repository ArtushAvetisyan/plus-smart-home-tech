package ru.yandex.practicum.commerce.interaction.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookedProductsDto {

    @NotNull(message = "Общий вес доставки должен быть указан")
    private Double deliveryWeight;

    @NotNull(message = "Общий объём доставки должен быть указан")
    private Double deliveryVolume;

    @NotNull(message = "Параметр хрупости должен быть указан")
    private Boolean fragile;
}