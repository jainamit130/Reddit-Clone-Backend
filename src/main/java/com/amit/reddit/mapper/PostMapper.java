package com.amit.reddit.mapper;

import com.amit.reddit.dto.PostRequestDto;
import com.amit.reddit.dto.PostResponseDto;
import com.amit.reddit.model.Community;
import com.amit.reddit.model.Post;
import com.amit.reddit.model.User;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(target="creationDate",expression="java(java.time.Instant.now())")
    @Mapping(target="description",source="postDto.description")
    @Mapping(target="user",source="user")
    @Mapping(target="community",source="community")
    Post mapDtoToPost(PostRequestDto postDto, Community community, User user);

    @InheritInverseConfiguration
    @Mapping(target="userName",source="user.username")
    @Mapping(target="communityName",source="community.communityName")
    @Mapping(target="votes",source="votes")
    @Mapping(target="comments",source="comments")
    PostResponseDto mapPostToDto(Post post);
}
