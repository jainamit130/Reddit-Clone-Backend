package com.amit.reddit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileDto {
    private List<PostResponseDto> posts;
    private List<CommentDto> comments;
}
