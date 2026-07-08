package ru.yandex.practicum.commerce.shopping.store.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.yandex.practicum.commerce.interaction.dto.store.PageProductDto;
import ru.yandex.practicum.commerce.interaction.dto.store.ProductDto;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PageableMapper {

    PageProductDto toPageProductDto(Page<ProductDto> productPage);

    default Pageable toPageable(int page, int size, List<String> sort) {
        List<Sort.Order> orders = new ArrayList<>();
        if (sort != null) {
            for (String sortParam : sort) {
                String[] parts = sortParam.split(",");
                String property = parts[0];

                Sort.Direction direction = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1]))
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

                orders.add(new Sort.Order(direction, property));
            }
        }
        return PageRequest.of(page, size, Sort.by(orders));
    }
}