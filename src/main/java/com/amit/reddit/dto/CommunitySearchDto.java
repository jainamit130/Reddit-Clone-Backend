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
public class CommunitySearchDto {
    @NotBlank(message = "Community name is required")
    private String communityName;
    @NotBlank
    private Long communityId;
    private Integer numberOfMembers;
}
