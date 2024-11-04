package org.kiennguyenfpt.datingapp.services;

import org.kiennguyenfpt.datingapp.entities.Event;

import java.util.List;

public interface EventService {
    Event createEvent(Event event);
    Event updateEvent(Long eventId, Event event);
    void deleteEvent(Long eventId);
    List<Event> getAllEvents();
}
