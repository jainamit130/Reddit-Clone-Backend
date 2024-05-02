package com.amit.reddit.mapper;

import com.amit.reddit.dto.CommunityDto;
import com.amit.reddit.model.Community;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommunityMapper {

    @Mapping(target = "communityId", expression = "java(mapCommunityId(community.getCommunityId()))")
    @Mapping(target = "user",source = "creatorUser")
    @Mapping(target = "posts",ignore = true)
    CommunityDto mapCommunityToDto(Community community);

    default Long mapCommunityId(Long communityId) {
        return communityId;
    }

    @InheritInverseConfiguration
    @Mapping(target = "posts",ignore = true)
    Community mapDtoToCommunity(CommunityDto communityDto);
}