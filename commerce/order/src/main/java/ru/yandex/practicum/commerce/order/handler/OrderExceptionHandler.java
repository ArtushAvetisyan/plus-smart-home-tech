package ru.yandex.practicum.commerce.order.handler;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.commerce.interaction.exception.NoOrderFoundException;
import ru.yandex.practicum.commerce.interaction.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.interaction.exception.NotAuthorizedUserException;
import ru.yandex.practicum.commerce.interaction.exception.response.ErrorResponse;

@RestControllerAdvice
public class OrderExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoOrderFoundException(NoOrderFoundException exception) {
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .userMessage(exception.getUserMessage())
                .httpStatus(HttpStatus.NOT_FOUND.name())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleUnauthorizedUserException(NotAuthorizedUserException exception) {
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .userMessage(exception.getUserMessage())
                .httpStatus(HttpStatus.UNAUTHORIZED.name())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoSpecifiedProductInWarehouseException(NoSpecifiedProductInWarehouseException exception) {
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .userMessage(exception.getUserMessage())
                .httpStatus(HttpStatus.NOT_FOUND.name())
                .build();
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleCircuitBreakerOpen(CallNotPermittedException exception) {
        String circuitBreakerName = exception.getCausingCircuitBreakerName();
        String userMessage = "Внутренний сервис магазина временно недоступен. Пожалуйста, повторите попытку позже";

        if ("delivery".equalsIgnoreCase(circuitBreakerName)) {
            userMessage = "Сервис доставки временно недоступен. Пожалуйста, попробуйте рассчитать или оформить доставку позже";
        } else if ("payment".equalsIgnoreCase(circuitBreakerName)) {
            userMessage = "Сервис оплаты временно недоступен. Пожалуйста, попробуйте провести платеж позже";
        } else if ("warehouse".equalsIgnoreCase(circuitBreakerName)) {
            userMessage = "Сервис склада временно недоступен. Пожалуйста, повторите попытку позже";
        }
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .userMessage(userMessage)
                .httpStatus(HttpStatus.SERVICE_UNAVAILABLE.name())
                .build();
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorResponse handleFeignException(FeignException exception) {
        String url = exception.request() != null ? exception.request().url() : "";
        String userMessage = "Не удалось связаться со смежным сервисом магазина. Пожалуйста, повторите попытку позже";

        if (url.contains("delivery")) {
            userMessage = "Не удалось связаться с сервисом доставки. Пожалуйста, повторите попытку позже";
        } else if (url.contains("payment")) {
            userMessage = "Не удалось связаться с сервисом оплаты. Пожалуйста, повторите попытку позже";
        } else if (url.contains("warehouse")) {
            userMessage = "Не удалось связаться с сервисом склада. Пожалуйста, повторите попытку позже";
        }

        return ErrorResponse.builder()
                .message(exception.getMessage())
                .userMessage(userMessage)
                .httpStatus(HttpStatus.BAD_GATEWAY.name())
                .build();
    }
}