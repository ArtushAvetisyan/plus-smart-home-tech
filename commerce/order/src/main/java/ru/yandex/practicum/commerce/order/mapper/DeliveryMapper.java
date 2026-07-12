package ru.yandex.practicum.commerce.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.commerce.interaction.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.interaction.dto.delivery.DeliveryState;
import ru.yandex.practicum.commerce.interaction.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.interaction.dto.warehouse.AddressDto;

import java.util.UUID;

@Mapper(componentModel = "spring", imports = {UUID.class, DeliveryState.class})
public interface DeliveryMapper {

    @Mapping(target = "deliveryId", expression = "java(UUID.randomUUID())")
    @Mapping(target = "fromAddress", source = "fromAddress")
    @Mapping(target = "toAddress", source = "request.deliveryAddress")
    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "deliveryState", expression = "java(DeliveryState.CREATED)")
    DeliveryDto toDeliveryDto(CreateNewOrderRequest request, UUID orderId, AddressDto fromAddress);
}