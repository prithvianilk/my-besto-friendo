package com.prithvianilk.mybestofriendo.contextservice.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import lombok.Builder;

@Builder
public record CalendarEvent(String summary, String description, Instant startTime, Instant endTime) {
    @Override
    public Instant endTime() {
        if (Objects.isNull(endTime)) {
            return startTime.plus(30, ChronoUnit.MINUTES);
        }
        return endTime;
    }
}
