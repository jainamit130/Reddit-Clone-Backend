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
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
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

    public CommentDto(Long commentId, Long postId, Instant creationDate, String comment, Long parentId, String username, List<CommentDto> replies, Integer repliesCount) {
        this.commentId = commentId;
        this.postId = postId;
        this.creationDate = creationDate;
        this.comment = comment;
        this.parentId = parentId;
        this.username = username;
        this.replies = replies;
        this.repliesCount = repliesCount;
        // Set default value if currentVote is not provided
        this.currentVote = VoteType.NOVOTE;
    }

    public void addReply(CommentDto reply){
        if(replies == null){
            replies = new ArrayList<>();
        }
        replies.add(reply);
    }
}
