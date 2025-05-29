package com.mapper;

import com.DTO.CommunityDTO;
import com.entity.Group.Community;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommunityMapper {

    @Mapping(source = "id", target = "communityId")
    @Mapping(source = "name", target = "communityName")
    @Mapping(source = "coverPhoto", target = "coverPhoto")
    @Mapping(source = "privacy", target = "privacy")
    CommunityDTO toDTO(Community community);
}
