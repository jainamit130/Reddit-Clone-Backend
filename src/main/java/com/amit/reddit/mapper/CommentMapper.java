package com.amit.reddit.mapper;

import com.amit.reddit.dto.CommentDto;
import com.amit.reddit.model.Comment;
import com.amit.reddit.model.Post;
import com.amit.reddit.model.User;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "postId", source = "post.postId")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "parentId", source = "parentComment.commentId")
    @Mapping(target = "replies", expression = "java(new ArrayList<>())")
    CommentDto mapCommentToDto(Comment comment);

    @InheritInverseConfiguration
    @Mapping(target = "post", source = "post")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "votes",ignore = true)
    @Mapping(target="creationDate",expression="java(java.time.Instant.now())")
    Comment mapDtoToComment(CommentDto commentDto, Post post, User user);
}
