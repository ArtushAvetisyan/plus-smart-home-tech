package controller;

import dto.hub.HubEvent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.hub.HubEventService;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class HubEventController {
    private final HubEventService hubEventService;

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent hubEvent) {
        hubEventService.processEvent(hubEvent);
    }
}