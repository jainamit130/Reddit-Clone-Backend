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

    public void addReply(CommentDto reply){
        if(replies == null){
            replies = new ArrayList<>();
        }
        replies.add(reply);
    }
}
