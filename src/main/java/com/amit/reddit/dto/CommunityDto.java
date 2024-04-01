package com.amit.reddit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunityDto {
    private Long communityId;
    @NotBlank(message = "Community name is required")
    private String communityName;
    @NotBlank(message = "Description is required")
    private String description;
    private Integer numberOfPosts;
}