package ru.yandex.practicum.commerce.interaction.dto.store;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private UUID productId;

    @NotBlank(message = "Наименование товара не может быть пустым")
    private String productName;

    @NotBlank(message = "Описание товара не может быть пустым")
    private String description;
    private String imageSrc;

    @NotNull(message = "Статус, перечисляющий состояние остатка должен быть указан")
    private QuantityState quantityState;

    @NotNull(message = "Статус товара должен быть указан")
    private ProductState productState;
    private ProductCategory productCategory;

    @NotNull(message = "Цена товара должна быть указана")
    private BigDecimal price;
}