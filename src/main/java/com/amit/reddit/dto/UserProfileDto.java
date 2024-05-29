package com.amit.reddit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileDto {
    private String userName;
    private Integer numberOfPosts;
    private Integer numberOfComments;
    private Instant joinDate;
    private List<PostResponseDto> posts;
    private List<CommentDto> comments;
}
