package ru.yandex.practicum.commerce.interaction.dto.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SortObject {

    private String direction;
    private String nullHandling;
    private Boolean ascending;
    private String property;
    private Boolean ignoreCase;
}