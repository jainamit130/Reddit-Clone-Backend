package com.amit.reddit.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voteId;
    private VoteType voteType;
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="userId",referencedColumnName = "userId")
    private User user;

    @Override
    public String toString() {
        return "Vote{" +
                "voteId=" + voteId +
                ", voteType=" + voteType.toString() +
                '}';
    }

    @NotNull
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="postId",referencedColumnName = "postId")
    private Post post;
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="commentId",referencedColumnName = "commentId")
    private Comment comment;
}
