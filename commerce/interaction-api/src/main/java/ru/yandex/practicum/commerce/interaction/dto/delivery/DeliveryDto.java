package ru.yandex.practicum.commerce.interaction.dto.delivery;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.commerce.interaction.dto.warehouse.AddressDto;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDto {

    @NotNull(message = "Идентификатор доставки не может быть пустым")
    private UUID deliveryId;

    @NotNull(message = "Адрес отправления (склада) не может быть пустым")
    private AddressDto fromAddress;

    @NotNull(message = "Адрес доставки (клиента) не может быть пустым")
    private AddressDto toAddress;

    @NotNull(message = "Идентификатор заказа не может быть пустым")
    private UUID orderId;

    @NotNull(message = "Статус доставки обязателен")
    private DeliveryState deliveryState;
}