package com.amit.reddit.mapper;

import com.amit.reddit.dto.CommunityDto;
import com.amit.reddit.model.Community;
import com.amit.reddit.model.Post;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommunityMapper {

    @Mapping(target = "numberOfPosts", expression = "java(mapPosts(community.getPosts()))")
    @Mapping(target = "communityId", expression = "java(mapCommunityId(community.getCommunityId()))")
    CommunityDto mapCommunityToDto(Community community);

    default Integer mapPosts(List<Post> numberOfPosts) {
        return numberOfPosts.size();
    }

    default Long mapCommunityId(Long communityId) {
        return communityId;
    }

    @InheritInverseConfiguration
    @Mapping(target = "posts", ignore = true)
    Community mapDtoToCommunity(CommunityDto communityDto);
}