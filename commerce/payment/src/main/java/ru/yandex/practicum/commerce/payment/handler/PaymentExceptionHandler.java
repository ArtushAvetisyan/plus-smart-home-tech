package ru.yandex.practicum.commerce.payment.handler;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.commerce.interaction.exception.NoOrderFoundException;
import ru.yandex.practicum.commerce.interaction.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.commerce.interaction.exception.response.ErrorResponse;

@RestControllerAdvice
public class PaymentExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotEnoughInfoInOrderToCalculateException(NotEnoughInfoInOrderToCalculateException exception) {
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .userMessage(exception.getUserMessage())
                .httpStatus(HttpStatus.BAD_REQUEST.name())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoOrderFoundException(NoOrderFoundException exception) {
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
        String userMessage = "Не удалось связаться со смежным сервисом. Пожалуйста, повторите попытку позже";

        if ("order".equalsIgnoreCase(circuitBreakerName)) {
            userMessage = "Сервис заказов временно недоступен. Пожалуйста, повторите попытку позже";
        } else if ("shopping-store".equalsIgnoreCase(circuitBreakerName)) {
            userMessage = "Сервис каталога товаров временно недоступен. Пожалуйста, повторите попытку позже";
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
        String userMessage = "Не удалось связаться со смежным сервисом. Пожалуйста, повторите попытку позже";

        if (url.contains("order")) {
            userMessage = "Не удалось связаться с сервисом заказов. Проверьте параметры или повторите попытку";
        } else if (url.contains("shopping-store") || url.contains("shoppingstore")) {
            userMessage = "Не удалось связаться с сервисом каталога товаров. Проверка актуальных цен временно невозможна";
        }
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .userMessage(userMessage)
                .httpStatus(HttpStatus.BAD_GATEWAY.name())
                .build();
    }
}