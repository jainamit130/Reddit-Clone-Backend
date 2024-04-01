package com.amit.reddit.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

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
    private Instant creationDate;
}
