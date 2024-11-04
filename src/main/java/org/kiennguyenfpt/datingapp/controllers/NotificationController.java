package org.kiennguyenfpt.datingapp.controllers;

import org.kiennguyenfpt.datingapp.services.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class NotificationController {

    private final SseService sseService;

    @Autowired
    public NotificationController(SseService sseService) {
        this.sseService = sseService;
    }

    @GetMapping("/api/subscribe")
    public SseEmitter subscribe() {
        return sseService.subscribe();
    }
}
