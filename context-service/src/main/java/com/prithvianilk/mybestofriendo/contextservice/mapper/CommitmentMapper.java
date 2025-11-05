package com.prithvianilk.mybestofriendo.contextservice.mapper;

import com.prithvianilk.mybestofriendo.contextservice.model.Commitment;
import com.prithvianilk.mybestofriendo.contextservice.model.CommitmentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommitmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    CommitmentEntity toEntity(Commitment commitment);
}

