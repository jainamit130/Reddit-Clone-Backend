package com.amit.reddit.service;

import com.amit.reddit.dto.UserProfileDto;
import com.amit.reddit.dto.UserSearchResponse;
import com.amit.reddit.exceptions.redditUserNotFoundException;
import com.amit.reddit.model.User;
import com.amit.reddit.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final PostService postService;
    private final CommentService commentService;
    private final UserRepository userRepository;

    public UserProfileDto getUserProfileDetails(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()->new redditUserNotFoundException());
        return UserProfileDto.builder()
               .posts(postService.getPostsByUsername(user.getUsername()))
               .comments(commentService.getAllUserComments(user.getUsername()))
               .build();
    }

    public List<UserSearchResponse> getAllSearchedUsers(String searchQuery) {
        List<User> searchedUsers = userRepository.findByUsernameContainsAndVerifiedTrue(searchQuery);
        return searchedUsers.stream().map(user -> {
               return UserSearchResponse.builder().userId(user.getUserId())
               .joinDate(user.getCreationDate()).userName(user.getUsername()).build();
        }).collect(Collectors.toList());
    }
}
