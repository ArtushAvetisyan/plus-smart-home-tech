package ru.yandex.practicum.commerce.delivery.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.commerce.delivery.entity.Delivery;
import ru.yandex.practicum.commerce.interaction.dto.delivery.DeliveryDto;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {
    Delivery toDelivery(DeliveryDto deliveryDto);

    DeliveryDto toDeliveryDto(Delivery delivery);
}