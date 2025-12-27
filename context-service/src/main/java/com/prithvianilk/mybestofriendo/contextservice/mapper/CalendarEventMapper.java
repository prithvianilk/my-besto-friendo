package com.prithvianilk.mybestofriendo.contextservice.mapper;

import com.prithvianilk.mybestofriendo.contextservice.model.CalendarEvent;
import com.prithvianilk.mybestofriendo.contextservice.model.Commitment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CalendarEventMapper {

    @Mapping(target = "summary", source = "description")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "startTime", source = "toBeCompletedAt")
    CalendarEvent toCalendarEvent(Commitment commitment);
}
