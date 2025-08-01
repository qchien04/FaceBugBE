package com.mapper;

import com.DTO.ProfileSummary;
import com.entity.auth.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "avt", target = "avt")
    @Mapping(source = "accountType", target = "accountType")
    ProfileSummary toProfileSummary(UserProfile profile);

    List<ProfileSummary> toProfileSummaries(List<UserProfile> profiles);
}
