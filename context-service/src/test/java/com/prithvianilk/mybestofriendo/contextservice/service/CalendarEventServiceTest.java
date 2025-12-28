package com.prithvianilk.mybestofriendo.contextservice.service;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.prithvianilk.mybestofriendo.contextservice.model.CalendarEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalendarEventServiceTest {

    @Mock
    private Calendar calendar;

    @Mock
    private Calendar.Events events;

    @Mock
    private Calendar.Events.Insert insert;

    @InjectMocks
    private CalendarEventService calendarEventService;

    @Test
    void createEvent_shouldCallInsertWithCorrectEvent() throws IOException {
        Event expectedEvent = createExpectedEvent();
        when(calendar.events()).thenReturn(events);
        when(events.insert("primary", expectedEvent)).thenReturn(insert);
        when(insert.execute()).thenReturn(new Event().setId("event-123"));

        CalendarEvent calendarEvent = CalendarEvent.builder()
                .summary("Test Summary")
                .description("Test Description")
                .startTime(Instant.parse("2025-01-01T10:00:00Z"))
                .endTime(Instant.parse("2025-01-01T11:00:00Z"))
                .build();

        calendarEventService.createEvent(calendarEvent);

        verify(events).insert("primary", expectedEvent);
        verify(insert).execute();
    }

    private Event createExpectedEvent() {
        List<EventReminder> reminders = Stream
                .of(30, 60, 180, 720, 1440)
                .map(minutes -> new EventReminder().setMinutes(minutes))
                .toList();

        return new Event()
                .setSummary("Test Summary")
                .setDescription("Test Description")
                .setStart(new EventDateTime()
                        .setDateTime(new DateTime(Instant.parse("2025-01-01T10:00:00Z").toEpochMilli()))
                        .setTimeZone("Asia/Kolkata"))
                .setEnd(new EventDateTime()
                        .setDateTime(new DateTime(Instant.parse("2025-01-01T11:00:00Z").toEpochMilli()))
                        .setTimeZone("Asia/Kolkata"))
                .setReminders(new Event.Reminders().setOverrides(reminders));
    }
}
