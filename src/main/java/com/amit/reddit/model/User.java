package com.amit.reddit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank(message = "Username is required")
    private String username;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Community> communities;

    @JsonIgnore
    @OneToMany(mappedBy = "creatorUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Community> createdCommunities;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY)
    private List<Post> posts;

    @NotBlank(message = "Password is required")
    private String password;

    @Email
    private String email;

    private Instant creationDate;

    private boolean verified;

    @Enumerated(EnumType.STRING)
    private Role role;

    public void addCommunity(Community community){
        communities.add(community);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return verified;
    }

    public void leaveCommunity(Community community) {
        communities.remove(community);
    }
}
