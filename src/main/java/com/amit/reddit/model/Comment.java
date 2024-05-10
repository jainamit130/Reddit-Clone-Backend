package com.amit.reddit.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @NotEmpty
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userId" , referencedColumnName = "userId")
    private User user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="postId", referencedColumnName = "postId")
    private Post post;

    private Integer votes;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> replies;

    private Integer repliesCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentCommentId")
    private Comment parentComment;

    private Instant creationDate;

    public void addReply(Comment reply){
        if(replies == null){
            replies = new ArrayList<>();
        }
        replies.add(reply);
        repliesCount++;
    }
}
