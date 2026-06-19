package dto.sensor;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Timestamp;
import java.time.Instant;

@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ClimateSensorEvent.class, name = "CLIMATE_SENSOR"),
        @JsonSubTypes.Type(value = LightSensorEvent.class, name = "LIGHT_SENSOR"),
        @JsonSubTypes.Type(value = MotionSensorEvent.class, name = "MOTION_SENSOR"),
        @JsonSubTypes.Type(value = SwitchSensorEvent.class, name = "SWITCH_SENSOR"),
        @JsonSubTypes.Type(value = TemperatureSensorEvent.class, name = "TEMPERATURE_SENSOR")
})
public abstract class SensorEvent {

    @NotBlank(message = "ID не может быть пустым")
    private String id;

    @NotBlank(message = "HubID не может быть пустым")
    private String hubId;

    @NotNull(message = "Временная метка события не может быть пустой")
    private Instant timestamp;

    @NotNull
    private SensorEventType type;

    public abstract SensorEventType getType();
}