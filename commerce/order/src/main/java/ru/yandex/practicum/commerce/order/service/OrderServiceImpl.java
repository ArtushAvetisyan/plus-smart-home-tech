package ru.yandex.practicum.commerce.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.interaction.client.DeliveryClient;
import ru.yandex.practicum.commerce.interaction.client.PaymentClient;
import ru.yandex.practicum.commerce.interaction.client.WarehouseClient;
import ru.yandex.practicum.commerce.interaction.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.interaction.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.interaction.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.interaction.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interaction.dto.order.OrderState;
import ru.yandex.practicum.commerce.interaction.dto.order.ProductReturnRequest;
import ru.yandex.practicum.commerce.interaction.dto.warehouse.AddressDto;
import ru.yandex.practicum.commerce.interaction.exception.NoOrderFoundException;
import ru.yandex.practicum.commerce.interaction.exception.NotAuthorizedUserException;
import ru.yandex.practicum.commerce.order.entity.Order;
import ru.yandex.practicum.commerce.order.mapper.DeliveryMapper;
import ru.yandex.practicum.commerce.order.mapper.OrderMapper;
import ru.yandex.practicum.commerce.order.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final WarehouseClient warehouseClient;
    private final DeliveryClient deliveryClient;
    private final PaymentClient paymentClient;
    private final DeliveryMapper deliveryMapper;
    private final OrderMapper orderMapper;

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getClientOrders(String username) {
        checkUsername(username);
        return orderRepository.findAllByUsername(username).stream()
                .map(orderMapper::toOrderDto)
                .toList();
    }

    @Override
    public OrderDto createNewOrder(String username, CreateNewOrderRequest request) {
        checkUsername(username);
        if (request == null) throw new IllegalArgumentException("Отправлен пустой запрос. Создание заказа невозможно");

        ShoppingCartDto cart = request.getShoppingCart();
        warehouseClient.assemblyProductsForOrder(cart);

        Map<UUID, Integer> cartProducts = (cart.getProducts() != null) ? cart.getProducts() : new HashMap<>();
        if (request.getDeliveryAddress() == null) {
            throw new IllegalArgumentException("Адрес доставки не может быть пустым");
        }

        Order order = orderMapper.toOrder(username, cart, cartProducts);
        AddressDto fromAddress = warehouseClient.getWarehouseAddress();
        DeliveryDto deliveryDto = deliveryMapper.toDeliveryDto(request, order.getOrderId(), fromAddress);

        DeliveryDto plannedDelivery = deliveryClient.planDelivery(deliveryDto);
        order.setDeliveryId(plannedDelivery.getDeliveryId());
        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest request) {
        if (request == null || request.getOrderId() == null) {
            throw new IllegalArgumentException("Запрос на возврат не может быть пустым");
        }

        Order order = findOrderByIdOrThrow(request.getOrderId());
        order.setState(OrderState.PRODUCT_RETURNED);
        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto paymentSuccess(UUID orderId) {
        Order order = findOrderByIdOrThrow(orderId);
        order.setState(OrderState.PAID);
        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        Order order = findOrderByIdOrThrow(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        Order order = findOrderByIdOrThrow(orderId);
        order.setState(OrderState.DELIVERED);
        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        Order order = findOrderByIdOrThrow(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto complete(UUID orderId) {
        Order order = findOrderByIdOrThrow(orderId);
        order.setState(OrderState.COMPLETED);
        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        Order order = findOrderByIdOrThrow(orderId);
        OrderDto orderDto = orderMapper.toOrderDto(order);

        BigDecimal productCost = paymentClient.productCost(orderDto);
        order.setProductPrice(productCost.doubleValue());

        OrderDto updatedOrderDto = orderMapper.toOrderDto(order);
        BigDecimal totalCost = paymentClient.getTotalCost(updatedOrderDto);
        order.setTotalPrice(totalCost.doubleValue());

        OrderDto finalOrderDto = orderMapper.toOrderDto(order);
        paymentClient.payment(finalOrderDto);

        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        Order order = findOrderByIdOrThrow(orderId);
        OrderDto orderDto = orderMapper.toOrderDto(order);
        BigDecimal deliveryCost = deliveryClient.deliveryCost(orderDto);
        order.setDeliveryPrice(deliveryCost.doubleValue());
        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        Order order = findOrderByIdOrThrow(orderId);
        order.setState(OrderState.ASSEMBLED);
        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        Order order = findOrderByIdOrThrow(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    private Order findOrderByIdOrThrow(UUID orderId) {
        if (orderId == null) throw new IllegalArgumentException("Идентификатор заказа не может быть пустым");
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ с идентификатором " + orderId + " не найден",
                        "Заказ с идентификатором " + orderId + " отсутствует"));
    }

    private void checkUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не может быть пустым", "Указан пустое имя пользователя");
        }
    }
}