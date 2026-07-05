package ru.yandex.practicum.commerce.warehouse.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.commerce.interaction.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.warehouse.entity.WarehouseProduct;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {

    @Mapping(target = "quantity", constant = "0L")
    @Mapping(target = "dimension.width", source = "request.dimension.width")
    @Mapping(target = "dimension.height", source = "request.dimension.height")
    @Mapping(target = "dimension.depth", source = "request.dimension.depth")
    WarehouseProduct toWarehouseProduct(NewProductInWarehouseRequest request);
}