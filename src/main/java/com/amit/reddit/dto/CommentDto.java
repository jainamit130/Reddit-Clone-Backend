package com.amit.reddit.dto;

import com.amit.reddit.model.Comment;
import com.amit.reddit.model.VoteType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class CommentDto {
    private Long commentId;
    @NotEmpty
    private Long postId;
    private Instant creationDate;
    @NotBlank
    private String comment;
    private Long parentId;
    private String username;
    List<CommentDto> replies;
    private Integer repliesCount;
    private VoteType currentVote;
    private Integer votes;
    private Boolean isDeleted;


    public void addReply(CommentDto reply){
        replies.add(reply);
    }
}
