package ru.yandex.practicum.commerce.interaction.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DimensionDto {

    @NotNull(message = "Ширина должна быть указана")
    private Double width;

    @NotNull(message = "Высота должна быть указана")
    private Double height;

    @NotNull(message = "Глубина должна быть указана")
    private Double depth;
}