package com.mapper;

import com.DTO.CommunityUserprofileDTO;
import com.entity.Group.CommunityUserprofile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommunityUserprofileMapper {

    @Mapping(source = "userProfile.id", target = "userId")
    @Mapping(source = "userProfile.name", target = "name")
    @Mapping(source = "userProfile.avt", target = "avt")
    @Mapping(source = "communityRole", target = "role")
    @Mapping(source = "joinAt", target = "joinAt")
    CommunityUserprofileDTO toDTO(CommunityUserprofile community);

    List<CommunityUserprofileDTO> toDTOs(List<CommunityUserprofile> communityUserprofiles);
}
