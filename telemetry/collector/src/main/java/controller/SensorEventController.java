package controller;

import dto.sensor.SensorEvent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.sensor.SensorEventService;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class SensorEventController {
    private final SensorEventService sensorEventService;

    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent sensorEvent) {
        sensorEventService.processEvent(sensorEvent);
    }
}