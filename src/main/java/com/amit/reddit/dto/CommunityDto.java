package com.amit.reddit.dto;

import com.amit.reddit.model.Post;
import com.amit.reddit.model.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunityDto {
    private Long communityId;
    @NotBlank(message = "Community name is required")
    private String communityName;
    private User user;
    @NotBlank(message = "Description is required")
    private String description;
    private Integer numberOfPosts;
    private List<Post> posts;
    private Integer numberOfMembers;
}