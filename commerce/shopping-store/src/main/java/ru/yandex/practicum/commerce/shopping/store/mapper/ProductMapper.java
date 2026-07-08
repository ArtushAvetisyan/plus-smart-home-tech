package ru.yandex.practicum.commerce.shopping.store.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.yandex.practicum.commerce.interaction.dto.store.ProductDto;
import ru.yandex.practicum.commerce.shopping.store.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDto toProductDto(Product product);

    @Mapping(target = "productId", ignore = true)
    Product toProduct(ProductDto productDto);

    @Mapping(target = "productId", ignore = true)
    void updateProductFromDto(ProductDto productDto, @MappingTarget Product product);
}