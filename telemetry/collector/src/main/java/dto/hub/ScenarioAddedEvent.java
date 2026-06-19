package dto.hub;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScenarioAddedEvent extends HubEvent {

    @NotBlank(message = "Название сценария не может быть пустым")
    @Min(value = 3, message = "Название добавленного сценария должно содержать не менее 3 символов")
    @Max(2147483647)
    private String name;

    @NotEmpty(message = "Список условий не может быть пустым")
    private List<ScenarioCondition> conditions;

    @NotEmpty(message = "Список действий не может быть пустым")
    private List<DeviceAction> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}