package ru.yandex.practicum.commerce.delivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.delivery.entity.Delivery;
import ru.yandex.practicum.commerce.delivery.mapper.DeliveryMapper;
import ru.yandex.practicum.commerce.delivery.repository.DeliveryRepository;
import ru.yandex.practicum.commerce.interaction.client.OrderClient;
import ru.yandex.practicum.commerce.interaction.client.WarehouseClient;
import ru.yandex.practicum.commerce.interaction.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.interaction.dto.delivery.DeliveryState;
import ru.yandex.practicum.commerce.interaction.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interaction.dto.warehouse.AddressDto;
import ru.yandex.practicum.commerce.interaction.dto.warehouse.ShippedToDeliveryRequest;
import ru.yandex.practicum.commerce.interaction.exception.NoDeliveryFoundException;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final WarehouseClient warehouseClient;
    private final OrderClient orderClient;
    private final DeliveryMapper mapper;

    @Override
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        Delivery delivery = mapper.toDelivery(deliveryDto);
        delivery.setDeliveryId(UUID.randomUUID());
        delivery.setDeliveryState(DeliveryState.CREATED);
        return mapper.toDeliveryDto(deliveryRepository.save(delivery));
    }

    @Override
    public void deliveryPicked(UUID orderId) {
        Delivery delivery = findDeliveryByOrderIdOrThrow(orderId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        deliveryRepository.save(delivery);
        warehouseClient.shippedToDelivery(new ShippedToDeliveryRequest(orderId, delivery.getDeliveryId()));
    }

    @Override
    public void deliverySuccessful(UUID orderId) {
        Delivery delivery = findDeliveryByOrderIdOrThrow(orderId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);
        orderClient.delivery(orderId);
    }

    @Override
    public void deliveryFailed(UUID orderId) {
        Delivery delivery = findDeliveryByOrderIdOrThrow(orderId);
        delivery.setDeliveryState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);
        orderClient.deliveryFailed(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateDeliveryCost(OrderDto orderDto) {
        Delivery delivery = findDeliveryByOrderIdOrThrow(orderDto.getOrderId());
        BigDecimal currentCost = BigDecimal.valueOf(5.0);

        AddressDto warehouseAddress = delivery.getFromAddress();
        AddressDto clientAddress = delivery.getToAddress();

        if (checkAddress(warehouseAddress, "ADDRESS_1")) {
            currentCost = currentCost.add(currentCost.multiply(BigDecimal.valueOf(1)));
        } else if (checkAddress(warehouseAddress, "ADDRESS_2")) {
            currentCost = currentCost.add(currentCost.multiply(BigDecimal.valueOf(2)));
        }

        if (Boolean.TRUE.equals(orderDto.getFragile())) {
            currentCost = currentCost.add(currentCost.multiply(BigDecimal.valueOf(0.2)));
        }

        if (orderDto.getDeliveryWeight() != null) {
            BigDecimal weight = BigDecimal.valueOf(orderDto.getDeliveryWeight());
            currentCost = currentCost.add(weight.multiply(BigDecimal.valueOf(0.3)));
        }

        if (orderDto.getDeliveryVolume() != null) {
            BigDecimal volume = BigDecimal.valueOf(orderDto.getDeliveryVolume());
            currentCost = currentCost.add(volume.multiply(BigDecimal.valueOf(0.2)));
        }

        String warehouseStreet = (warehouseAddress != null && warehouseAddress.getStreet() != null)
                ? warehouseAddress.getStreet() : "";
        String clientStreet = (clientAddress != null && clientAddress.getStreet() != null)
                ? clientAddress.getStreet() : "";

        if (!clientStreet.equalsIgnoreCase(warehouseStreet)) {
            currentCost = currentCost.add(currentCost.multiply(BigDecimal.valueOf(0.2)));
        }
        return currentCost;
    }

    private Delivery findDeliveryByOrderIdOrThrow(UUID orderId) {
        return deliveryRepository.findByOrderId(orderId).orElseThrow(() ->
                new NoDeliveryFoundException("Доставка с идентификатором заказа " + orderId + " не найдена",
                        "Доставка с идентификатором заказа " + orderId + " не найдена"));
    }

    private boolean checkAddress(AddressDto address, String target) {
        if (address == null) return false;
        return (address.getStreet() != null && address.getStreet().contains(target))
                || (address.getCity() != null && address.getCity().contains(target))
                || (address.getHouse() != null && address.getHouse().contains(target));
    }
}