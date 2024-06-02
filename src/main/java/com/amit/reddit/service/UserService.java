package com.amit.reddit.service;

import com.amit.reddit.dto.CommentDto;
import com.amit.reddit.dto.PostResponseDto;
import com.amit.reddit.dto.UserProfileDto;
import com.amit.reddit.dto.UserSearchResponse;
import com.amit.reddit.exceptions.redditUserNotFoundException;
import com.amit.reddit.model.Post;
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

    public UserProfileDto getUserProfileDetails(Long id) {
        User user = userRepository.findByUserIdAndVerifiedTrue(id)
                .orElseThrow(()->new redditUserNotFoundException());
        List<PostResponseDto> posts = postService.getPostsByUsername(user.getUsername());
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

    public void clearRecentPostList(){
        if(authService.isUserLoggedIn()){
            User user = authService.getCurrentUser();
            user.setRecentlyOpenedPosts(new LinkedList<>());
        } else {
            throw new redditUserNotFoundException();
        }
    }

    public List<UserSearchResponse> getAllSearchedUsers(String searchQuery) {
        List<User> searchedUsers = userRepository.findByUsernameContainsAndVerifiedTrue(searchQuery);
        return searchedUsers.stream().map(user -> UserSearchResponse.builder().userId(user.getUserId())
                .joinDate(user.getCreationDate()).userName(user.getUsername()).build()).collect(Collectors.toList());
    }

    public List<Post> getUserHistory() {
        if(authService.isUserLoggedIn()){
            User user=authService.getCurrentUser();
            return user.getRecentlyOpenedPosts();
        } else {
            throw new redditUserNotFoundException();
        }
    }
}
