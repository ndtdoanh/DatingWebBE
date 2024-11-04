package org.kiennguyenfpt.datingapp.controllers;

import org.kiennguyenfpt.datingapp.entities.Event;
import org.kiennguyenfpt.datingapp.responses.CommonResponse;
import org.kiennguyenfpt.datingapp.services.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/events")
@CrossOrigin
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/create")
    public ResponseEntity<CommonResponse<Event>> createEvent(@RequestBody Event event) {
        CommonResponse<Event> response = new CommonResponse<>();
        try {
            Event createdEvent = eventService.createEvent(event);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Event created successfully.");
            response.setData(createdEvent);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error creating event: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<CommonResponse<Event>> updateEvent(@RequestParam Long eventId, @RequestBody Event event) {
        CommonResponse<Event> response = new CommonResponse<>();
        try {
            Event updatedEvent = eventService.updateEvent(eventId, event);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Event updated successfully.");
            response.setData(updatedEvent);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error updating event: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<CommonResponse<String>> deleteEvent(@RequestParam Long eventId) {
        CommonResponse<String> response = new CommonResponse<>();
        try {
            eventService.deleteEvent(eventId);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Event deleted successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error deleting event: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<CommonResponse<List<Event>>> getAllEvents() {
        CommonResponse<List<Event>> response = new CommonResponse<>();
        try {
            List<Event> events = eventService.getAllEvents();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Events retrieved successfully.");
            response.setData(events);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error retrieving events: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
