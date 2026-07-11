package ru.yandex.practicum.commerce.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.commerce.warehouse.entity.OrderBooking;

import java.util.UUID;

public interface OrderBookingRepository extends JpaRepository<OrderBooking, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE OrderBooking b SET b.deliveryId = :deliveryId WHERE b.orderId = :orderId")
    int updateDeliveryIdByOrderId(@Param("orderId") UUID orderId, @Param("deliveryId") UUID deliveryId);
}