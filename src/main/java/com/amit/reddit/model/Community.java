package com.amit.reddit.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
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
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private Instant creationDate;
    @OneToMany(fetch = FetchType.LAZY)
    private List<Post> posts;

}
