package com.amit.reddit.dto;

import com.amit.reddit.model.Community;
import com.amit.reddit.model.Vote;
import com.amit.reddit.model.VoteType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {
    private Long postId;
    private String postName;
    private String description;
    private Instant creationDate;
    private String userName;
    private String communityName;
    private Integer votes;
    private Integer comments;
    private VoteType currentVote;
}
