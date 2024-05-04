package com.amit.reddit.service;

import com.amit.reddit.dto.UserProfileDto;
import com.amit.reddit.model.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final PostService postService;
    private final CommentService commentService;
    private final AuthService authService;

    public UserProfileDto getUserProfileDetails() {
        User user = authService.getCurrentUser();
        return UserProfileDto.builder()
               .posts(postService.getPostsByUsername(user.getUsername()))
               .comments(commentService.getAllUserComments(user.getUsername()))
               .build();
    }
}
