package com.amit.reddit.service;

import com.amit.reddit.dto.CommentDto;
import com.amit.reddit.dto.PostResponseDto;
import com.amit.reddit.dto.UserProfileDto;
import com.amit.reddit.dto.UserSearchResponse;
import com.amit.reddit.exceptions.redditUserNotFoundException;
import com.amit.reddit.model.User;
import com.amit.reddit.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
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
    private final AuthService authService;

    @Transactional(readOnly = true)
    public UserProfileDto getUserProfileDetails(Long id) {
        User user = userRepository.findByUserIdAndVerifiedTrue(id)
                .orElseThrow(()->new redditUserNotFoundException());
        List<PostResponseDto> posts = user.getPosts().stream().map(postService::postToPostResponse).collect(Collectors.toList());
        List<CommentDto> comments = commentService.getAllUserComments(user.getUsername());
        return UserProfileDto.builder()
                .userName(user.getUsername())
                .numberOfPosts(posts.size())
                .numberOfComments(comments.size())
                .joinDate(user.getCreationDate())
               .posts(posts)
               .comments(comments)
               .build();
    }

    @Transactional
    public void clearRecentPostList(){
        if(authService.isUserLoggedIn()){
            User user = authService.getCurrentUser();
            user.setRecentlyOpenedPosts(new LinkedList<>());
            userRepository.save(user);
        } else {
            throw new redditUserNotFoundException();
        }
    }

    @Transactional(readOnly = true)
    public List<UserSearchResponse> getAllSearchedUsers(String searchQuery) {
        List<User> searchedUsers = userRepository.findByUsernameContainsAndVerifiedTrueIgnoreCase(searchQuery);
        return searchedUsers.stream().map(user -> UserSearchResponse.builder().userId(user.getUserId())
                .joinDate(user.getCreationDate()).userName(user.getUsername()).build()).collect(Collectors.toList());
    }

    public List<PostResponseDto> getUserHistory() {
        if(authService.isUserLoggedIn()){
            User user=authService.getCurrentUser();
            return user.getRecentlyOpenedPosts()
                    .stream()
                    .map(post -> postService.postToPostResponse(post))
                    .collect(Collectors.toList());
        } else {
            throw new redditUserNotFoundException();
        }
    }
}
