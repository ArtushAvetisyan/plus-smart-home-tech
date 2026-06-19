package dto.hub;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScenarioRemovedEvent extends HubEvent {

    @NotBlank(message = "Название сценария не может быть пустым")
    @Min(value = 3, message = "Название удаляемого сценария должно содержать не менее 3 символов")
    @Max(2147483647)
    private String name;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED;
    }
}