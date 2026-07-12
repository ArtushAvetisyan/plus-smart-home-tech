package ru.yandex.practicum.commerce.interaction.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.interaction.dto.store.PageProductDto;
import ru.yandex.practicum.commerce.interaction.dto.store.ProductCategory;
import ru.yandex.practicum.commerce.interaction.dto.store.ProductDto;
import ru.yandex.practicum.commerce.interaction.dto.store.SetProductQuantityStateRequest;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface ShoppingStoreClient {

    @GetMapping
    PageProductDto getProducts(
            @RequestParam("category") @NotNull ProductCategory category,
            @RequestParam("page") @Min(0) int page,
            @RequestParam("size") @Positive int size,
            @RequestParam("sort") @NotEmpty List<String> sort
    );

    @GetMapping("/batch")
    List<ProductDto> getProductsByIds(@RequestBody @NotEmpty Set<UUID> productIds);

    @PutMapping
    ProductDto createNewProduct(@Valid @RequestBody ProductDto productDto);

    @PostMapping
    ProductDto updateProduct(@Valid @RequestBody ProductDto productDto);

    @PostMapping("/removeProductFromStore")
    Boolean removeProductFromStore(@RequestBody @NotNull UUID productId);

    @PostMapping("/quantityState")
    Boolean setProductQuantityState(@Valid @RequestBody SetProductQuantityStateRequest request);

    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable("productId") @NotNull UUID productId);
}