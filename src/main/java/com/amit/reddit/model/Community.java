package com.amit.reddit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Community {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long communityId;
    @Value("${community.type:public}")
    private CommunityType type;
    @NotBlank(message = "Community name is required")
    private String communityName;
    @NotBlank(message = "Description is required")
    @Lob
    @Column(length = 1024)
    private String description;
    @Column(name = "creator_user_id", updatable = false)
    private Long creatorUserId;
    @ManyToMany(fetch = FetchType.LAZY)
    private List<User> members;
    private Integer numberOfMembers;
    private Instant creationDate;
    @OneToMany(fetch = FetchType.LAZY)
    private List<Post> posts;
    private Integer numberOfPosts;

    public void addPost(Post post){
        if (posts == null)
            posts = new ArrayList<>();
        if(numberOfPosts==null)
            numberOfPosts=0;
        posts.add(post);
        numberOfPosts++;
    }

    public void addMember(User user){
        if (members == null)
            members = new ArrayList<>();
        if(numberOfMembers==null)
            numberOfMembers=0;
        members.add(user);
        numberOfMembers++;
    }

    public void removeUser(User user) {
        members.remove(user);
        numberOfMembers--;
    }


}
