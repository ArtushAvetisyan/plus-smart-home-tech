package ru.yandex.practicum.commerce.shopping.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.interaction.client.ShoppingStoreClient;
import ru.yandex.practicum.commerce.interaction.dto.store.PageProductDto;
import ru.yandex.practicum.commerce.interaction.dto.store.ProductCategory;
import ru.yandex.practicum.commerce.interaction.dto.store.ProductDto;
import ru.yandex.practicum.commerce.interaction.dto.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.commerce.shopping.store.mapper.PageableMapper;
import ru.yandex.practicum.commerce.shopping.store.service.ShoppingStoreService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController implements ShoppingStoreClient {
    private final ShoppingStoreService shoppingStoreService;
    private final PageableMapper pageableMapper;

    @Override
    @GetMapping
    public PageProductDto getProducts(@RequestParam ProductCategory category,
                                      @RequestParam int page,
                                      @RequestParam int size,
                                      @RequestParam List<String> sort) {
        Pageable pageable = pageableMapper.toPageable(page, size, sort);
        Page<ProductDto> productPage = shoppingStoreService.getProducts(category, pageable);
        return pageableMapper.toPageProductDto(productPage);
    }

    @Override
    @PutMapping
    public ProductDto createNewProduct(@RequestBody ProductDto productDto) {
        return shoppingStoreService.createNewProduct(productDto);
    }

    @Override
    @PostMapping
    public ProductDto updateProduct(@RequestBody ProductDto productDto) {
        return shoppingStoreService.updateProduct(productDto);
    }

    @Override
    @PostMapping("/removeProductFromStore")
    public Boolean removeProductFromStore(@RequestBody UUID productId) {
        return shoppingStoreService.removeProductFromStore(productId);
    }

    @Override
    @PostMapping("/quantityState")
    public Boolean setProductQuantityState(@RequestBody SetProductQuantityStateRequest request) {
        return shoppingStoreService.setProductQuantityState(request);
    }

    @Override
    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable UUID productId) {
        return shoppingStoreService.getProduct(productId);
    }
}