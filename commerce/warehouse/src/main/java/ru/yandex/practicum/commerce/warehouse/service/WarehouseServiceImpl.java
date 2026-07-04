package ru.yandex.practicum.commerce.warehouse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.interaction.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.interaction.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.interaction.dto.warehouse.AddressDto;
import ru.yandex.practicum.commerce.interaction.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.interaction.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.warehouse.entity.Dimension;
import ru.yandex.practicum.commerce.warehouse.entity.WarehouseProduct;
import ru.yandex.practicum.commerce.warehouse.handler.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.warehouse.handler.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.warehouse.handler.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.commerce.warehouse.mapper.WarehouseMapper;
import ru.yandex.practicum.commerce.warehouse.repository.WarehouseRepository;

import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {
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
                throw new ProductInShoppingCartLowQuantityInWarehouse("Недостаточно товара на складе (id - " + productId,
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
                new NoSpecifiedProductInWarehouseException("Продукт отсуствует на складе",
                        "Продукт отсуствует на складе"));

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
}