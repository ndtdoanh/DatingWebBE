package org.kiennguyenfpt.datingapp.services;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseService {
    SseEmitter subscribe();

    void sendNotification(String message);

}
