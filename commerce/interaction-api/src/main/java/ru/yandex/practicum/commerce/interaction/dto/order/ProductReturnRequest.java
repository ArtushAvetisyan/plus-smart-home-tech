package ru.yandex.practicum.commerce.interaction.dto.order;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReturnRequest {

    private UUID orderId;

    @NotEmpty(message = "Список товаров для возврата не должен быть пустым")
    private Map<UUID, Integer> products;
}