package com.prithvianilk.mybestofriendo.contextservice.mapper;

import com.prithvianilk.mybestofriendo.contextservice.model.CalendarEvent;
import com.prithvianilk.mybestofriendo.contextservice.model.Commitment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CalendarEventMapper {

    @Mapping(target = "summary", source = "commitment.description")
    @Mapping(target = "description", expression = "java(formatDescription(participantName, commitment.description()))")
    @Mapping(target = "startTime", source = "commitment.toBeCompletedAt")
    CalendarEvent toCalendarEvent(Commitment commitment, String participantName);

    @Named("formatDescription")
    default String formatDescription(String participantName, String description) {
        return "Commitment with " + participantName + ": " + description;
    }
}
