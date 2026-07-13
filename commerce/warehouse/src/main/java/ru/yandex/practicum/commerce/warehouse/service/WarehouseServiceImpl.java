package ru.yandex.practicum.commerce.warehouse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.interaction.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.interaction.dto.warehouse.*;
import ru.yandex.practicum.commerce.interaction.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.interaction.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.interaction.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.commerce.warehouse.entity.Dimension;
import ru.yandex.practicum.commerce.warehouse.entity.OrderBooking;
import ru.yandex.practicum.commerce.warehouse.entity.WarehouseProduct;
import ru.yandex.practicum.commerce.warehouse.mapper.WarehouseMapper;
import ru.yandex.practicum.commerce.warehouse.repository.OrderBookingRepository;
import ru.yandex.practicum.commerce.warehouse.repository.WarehouseRepository;

import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {
    private final OrderBookingRepository bookingRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    private static final String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

    @Override
    public void addProductInWarehouse(NewProductInWarehouseRequest request) {
        if (warehouseRepository.existsById(request.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException("Продукт уже зарегистрирован на складе",
                    "Данный продукт уже зарегистрирован на складе");
        }

        WarehouseProduct product = warehouseMapper.toWarehouseProduct(request);
        warehouseRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCartDto) {
        Set<UUID> productIds = shoppingCartDto.getProducts().keySet();
        List<WarehouseProduct> products = warehouseRepository.findAllById(productIds);

        Map<UUID, WarehouseProduct> productMap = products.stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));

        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean isFragile = false;

        for (Map.Entry<UUID, Integer> entry : shoppingCartDto.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            Integer requestedQuantity = entry.getValue();
            WarehouseProduct product = productMap.get(productId);

            if (product == null) {
                throw new ProductInShoppingCartLowQuantityInWarehouse("Продукт с id - " + productId + " отсутствует на складе",
                        "Некоторые из продуктов отсутствуют на складе");
            }

            if (product.getQuantity() < requestedQuantity) {
                throw new ProductInShoppingCartLowQuantityInWarehouse("Недостаточно товара на складе (id - " + productId + ")",
                        "Недостаточно товара на складе");
            }

            totalWeight += product.getWeight() * requestedQuantity;
            Dimension dimension = product.getDimension();
            double volume = dimension.getWidth() * dimension.getHeight() * dimension.getDepth();
            totalVolume += volume * requestedQuantity;

            if (product.getFragile()) {
                isFragile = true;
            }
        }
        return new BookedProductsDto(totalWeight, totalVolume, isFragile);
    }

    @Override
    public void receiveProductInWarehouse(AddProductToWarehouseRequest request) {
        WarehouseProduct product = warehouseRepository.findById(request.getProductId()).orElseThrow(() ->
                new NoSpecifiedProductInWarehouseException("Продукт отсутствует на складе",
                        "Продукт отсутствует на складе"));

        product.setQuantity(product.getQuantity() + request.getQuantity());
        warehouseRepository.save(product);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        AddressDto addressDto = new AddressDto();
        addressDto.setCountry(CURRENT_ADDRESS);
        addressDto.setCity(CURRENT_ADDRESS);
        addressDto.setStreet(CURRENT_ADDRESS);
        addressDto.setHouse(CURRENT_ADDRESS);
        addressDto.setFlat(CURRENT_ADDRESS);
        return addressDto;
    }

    @Override
    public BookedProductsDto assemblyProductsForOrder(ShoppingCartDto shoppingCartDto) {
        UUID cartId = shoppingCartDto.getShoppingCartId();
        Map<UUID, Integer> orderProducts = shoppingCartDto.getProducts();

        List<WarehouseProduct> warehouseProducts = warehouseRepository.findByProductIdIn(List.copyOf(orderProducts.keySet()));
        Map<UUID, WarehouseProduct> productMap = warehouseProducts.stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));

        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean hasFragileItems = false;

        List<OrderBooking> bookingsToSave = new ArrayList<>(orderProducts.size());

        for (Map.Entry<UUID, Integer> entry : orderProducts.entrySet()) {
            UUID productId = entry.getKey();
            long requiredQuantity = entry.getValue();

            WarehouseProduct warehouseProduct = productMap.get(productId);
            if (warehouseProduct == null) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(
                        "Товар с ID " + productId + " отсутствует на складе",
                        "Товар с ID " + productId + " отсутствует на складе");
            }

            if (warehouseProduct.getQuantity() < requiredQuantity) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(
                        "Недостаточно товара с ID " + productId + " на складе. Требуется: "
                                + requiredQuantity + ", в наличии: " + warehouseProduct.getQuantity(),
                        "Недостаточно товара на складе");
            }

            warehouseProduct.setQuantity(warehouseProduct.getQuantity() - requiredQuantity);
            totalWeight += warehouseProduct.getWeight() * requiredQuantity;

            Dimension dimension = warehouseProduct.getDimension();
            if (dimension != null) {
                double unitVolume = dimension.getWidth() * dimension.getHeight() * dimension.getDepth();
                totalVolume += unitVolume * requiredQuantity;
            }

            if (Boolean.TRUE.equals(warehouseProduct.getFragile())) {
                hasFragileItems = true;
            }

            OrderBooking booking = new OrderBooking();
            booking.setOrderId(cartId);
            booking.setProductId(productId);
            booking.setQuantity(requiredQuantity);
            bookingsToSave.add(booking);
        }
        warehouseRepository.saveAll(warehouseProducts);
        bookingRepository.saveAll(bookingsToSave);
        return new BookedProductsDto(totalWeight, totalVolume, hasFragileItems);
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        UUID orderId = request.getOrderId();
        UUID deliveryId = request.getDeliveryId();

        int updatedRows = bookingRepository.updateDeliveryIdByOrderId(orderId, deliveryId);
        if (updatedRows == 0) {
            throw new IllegalArgumentException("Не найдены забронированные товары для заказа: " + orderId);
        }
    }

    @Override
    public void acceptReturn(Map<UUID, Integer> returns) {
        List<WarehouseProduct> warehouseProducts = warehouseRepository.findByProductIdIn(List.copyOf(returns.keySet()));
        Map<UUID, WarehouseProduct> productMap = warehouseProducts.stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));

        for (Map.Entry<UUID, Integer> entry : returns.entrySet()) {
            UUID productId = entry.getKey();
            long returnQuantity = entry.getValue();

            WarehouseProduct warehouseProduct = productMap.get(productId);
            if (warehouseProduct == null) {
                throw new IllegalArgumentException("Товар с ID " + productId + " не зарегистрирован на складе.");
            }

            warehouseProduct.setQuantity(warehouseProduct.getQuantity() + returnQuantity);
        }
        warehouseRepository.saveAll(warehouseProducts);
    }
}