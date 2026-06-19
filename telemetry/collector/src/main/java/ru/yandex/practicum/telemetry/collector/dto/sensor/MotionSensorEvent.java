package ru.yandex.practicum.telemetry.collector.dto.sensor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MotionSensorEvent extends SensorEvent {

    int linkQuality;
    boolean motion;
    int voltage;

    @Override
    public SensorEventType getType() {
        return SensorEventType.MOTION_SENSOR;
    }
}