package ru.yandex.practicum.commerce.warehouse.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.commerce.warehouse.handler.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.warehouse.handler.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.warehouse.handler.exception.SpecifiedProductAlreadyInWarehouseException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNoSpecifiedProductInWarehouseException(NoSpecifiedProductInWarehouseException exception) {
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .userMessage(exception.getUserMessage())
                .httpStatus(HttpStatus.BAD_REQUEST.name())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleSpecifiedProductAlreadyInWarehouseException(SpecifiedProductAlreadyInWarehouseException exception) {
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .userMessage(exception.getUserMessage())
                .httpStatus(HttpStatus.BAD_REQUEST.name())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleProductInShoppingCartLowQuantityInWarehouse(ProductInShoppingCartLowQuantityInWarehouse exception) {
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .userMessage(exception.getUserMessage())
                .httpStatus(HttpStatus.BAD_REQUEST.name())
                .build();
    }
}