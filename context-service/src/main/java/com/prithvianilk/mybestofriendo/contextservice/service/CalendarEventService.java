package com.prithvianilk.mybestofriendo.contextservice.service;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.prithvianilk.mybestofriendo.contextservice.model.CalendarEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarEventService {
    private final Calendar calendarService;

    /**
     * Default event reminders at intervals before the start time:
     * 30m, 1h, 3h, 12h (half day), and 24h (1 day).
     */
    private static final List<EventReminder> EVENT_REMINDERS = Stream
            .of(30, 60, 3 * 60, 12 * 60, 24 * 60)
            .map(minutes -> new EventReminder().setMinutes(minutes))
            .toList();

    private static final String TIME_ZONE = "Asia/Kolkata";
    private static final String CALENDAR_ID = "primary";

    public String createEvent(CalendarEvent calendarEvent) {
        Event event = toGoogleEvent(calendarEvent);

        try {
            Event createdEvent = calendarService.events().insert(CALENDAR_ID, event).execute();
            log.info("Created calendar event: {} with ID: {}", calendarEvent.summary(), createdEvent.getId());
            return createdEvent.getId();
        } catch (IOException e) {
            log.error("Failed to create calendar event: {}", calendarEvent.summary(), e);
            throw new RuntimeException("Failed to create calendar event", e);
        }
    }

    public String updateEvent(String eventId, CalendarEvent calendarEvent) {
        try {
            Event existingEvent = calendarService.events().get(CALENDAR_ID, eventId).execute();
            updateGoogleEvent(existingEvent, calendarEvent);

            Event updatedEvent = calendarService.events().update(CALENDAR_ID, eventId, existingEvent).execute();
            log.info("Updated calendar event: {} with ID: {}", calendarEvent.summary(), updatedEvent.getId());
            return updatedEvent.getId();
        } catch (IOException e) {
            log.error("Failed to update calendar event with ID: {}", eventId, e);
            throw new RuntimeException("Failed to update calendar event", e);
        }
    }

    public void deleteEvent(String eventId) {
        try {
            calendarService.events().delete(CALENDAR_ID, eventId).execute();
            log.info("Deleted calendar event with ID: {}", eventId);
        } catch (IOException e) {
            log.error("Failed to delete calendar event with ID: {}", eventId, e);
            throw new RuntimeException("Failed to delete calendar event", e);
        }
    }

    private Event toGoogleEvent(CalendarEvent calendarEvent) {
        Event event = new Event()
                .setSummary(calendarEvent.summary())
                .setDescription(calendarEvent.description());

        EventDateTime start = getEventDateTime(calendarEvent.startTime());
        event.setStart(start);

        EventDateTime end = getEventDateTime(calendarEvent.endTime());
        event.setEnd(end);

        event.setReminders(new Event.Reminders().setOverrides(EVENT_REMINDERS));

        return event;
    }

    private static EventDateTime getEventDateTime(Instant timestamp) {
        return new EventDateTime()
                .setDateTime(new DateTime(timestamp.toEpochMilli()))
                .setTimeZone(TIME_ZONE);
    }

    private void updateGoogleEvent(Event event, CalendarEvent calendarEvent) {
        event.setSummary(calendarEvent.summary());
        event.setDescription(calendarEvent.description());

        EventDateTime start = getEventDateTime(calendarEvent.startTime());
        event.setStart(start);

        EventDateTime end = getEventDateTime(calendarEvent.endTime());
        event.setEnd(end);
    }
}
