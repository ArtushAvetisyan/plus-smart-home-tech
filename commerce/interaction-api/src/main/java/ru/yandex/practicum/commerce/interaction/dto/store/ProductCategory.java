package ru.yandex.practicum.commerce.interaction.dto.store;

public enum ProductCategory {
    LIGHTING,
    CONTROL,
    SENSORS;

    public static boolean isValid(String value) {
        if (value == null) return false;
        for (ProductCategory category : values()) {
            if (category.name().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}