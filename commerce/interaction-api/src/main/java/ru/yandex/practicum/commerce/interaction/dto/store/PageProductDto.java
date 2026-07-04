package ru.yandex.practicum.commerce.interaction.dto.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageProductDto {

    private Long totalElements;
    private Integer totalPages;
    private Boolean first;
    private Boolean last;
    private Integer size;
    private List<ProductDto> content;
    private Integer number;
    private List<SortObject> sort;
    private Integer numberOfElements;
    private PageableObject pageable;
    private Boolean empty;
}