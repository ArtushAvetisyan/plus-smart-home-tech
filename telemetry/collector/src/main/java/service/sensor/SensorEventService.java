package service.sensor;

import dto.sensor.SensorEvent;

public interface SensorEventService {
    void processEvent(SensorEvent sensorEvent);
}