package com.amit.reddit.service;

import com.amit.reddit.dto.PostRequestDto;
import com.amit.reddit.dto.PostResponseDto;
import com.amit.reddit.exceptions.communityNotFoundException;
import com.amit.reddit.exceptions.redditException;
import com.amit.reddit.exceptions.redditUserNotFoundException;
import com.amit.reddit.mapper.PostMapper;
import com.amit.reddit.model.*;
import com.amit.reddit.repository.CommunityRepository;
import com.amit.reddit.repository.PostRepository;
import com.amit.reddit.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
//@ConfigurationProperties(prefix = "reddit")
public class PostService {
    private final Integer recentlyOpenedPostsSize=10;
    private final CommunityRepository communityRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final VoteService voteService;
    private final AuthService authService;
    private final PostMapper postMapper;

    public PostResponseDto create(PostRequestDto postDto) {
        String communityName=postDto.getCommunityName();
        Community community = communityRepository.findByCommunityName(communityName)
                .orElseThrow(() -> new communityNotFoundException(communityName));

        User currentUser = authService.getCurrentUser();
        Post post=postMapper.mapDtoToPost(postDto,community,currentUser);
        post.setVotes(1);//On creating the post the vote is set by defualt to 1
        post.setComments(0);
        savePost(post);
        currentUser.addPost(post);
        community.addPost(post);
        userRepository.save(currentUser);
        communityRepository.save(community);
        voteService.saveDefaultVote(post);
        PostResponseDto postResponse = postToPostResponse(post);
        return postResponse;
    }

    @Transactional
    public PostResponseDto getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new redditException("Post not found!"));
//        if(authService.isUserLoggedIn()) {
//            User user = authService.getCurrentUser();
//            user.addRecentlyOpenedPost(post);
//            userRepository.save(user);
//        }
        PostResponseDto postResponse = postToPostResponse(post);
        return postResponse;
    }

    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(()->new redditException("Sorry post does not exist"));
        User user= authService.getCurrentUser();
        User postUser = post.getUser();
        if(user.equals(postUser)){
            post.setPostName("deleted");
            post.setDescription("deleted");
            user.deletePost(post);
            userRepository.save(user);
            post.setDeleted(true);
            postRepository.save(post);
        } else {
            throw new redditException("No such posts exists for user: "+user.getUsername());
        }
        return;
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> getAllPosts() {
        List<PostResponseDto> posts=postRepository.findAllByIsDeletedFalse()
                .stream()
                .map(post -> postToPostResponse(post))
                .collect(Collectors.toList());
        return posts;
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> getPostsByCommunity(Long id) {
        Community community=communityRepository.findById(id)
                .orElseThrow(() -> new communityNotFoundException(id.toString()));
        List<PostResponseDto> posts=postRepository.findAllByCommunity(community)
                .stream()
                .map(post -> postToPostResponse(post))
                .collect(Collectors.toList());
        return posts;
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> getPostsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new redditUserNotFoundException(username));
        List<PostResponseDto> posts=postRepository.findAllByUser(user)
                .stream()
                .map(post -> postToPostResponse(post))
                .collect(Collectors.toList());
        return posts;
    }

    public PostResponseDto postToPostResponse(Post post) {
        PostResponseDto postResponse = postMapper.mapPostToDto(post);
        postResponse.setCurrentVote(voteService.getUserVote(post,null));
        return postResponse;
    }

    public List<PostResponseDto> getAllSearchedPosts(String searchQuery){
        return postRepository.findByDescriptionContainingOrPostNameContaining(searchQuery.toLowerCase())
                .stream()
                .map(post -> {
                    return postToPostResponse(post);
                })
                .collect(Collectors.toList());
    }

    public Post getPostOfComment(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new redditException("Sorry! The post no longer exists"));
    }

    public void savePost(Post post) {
        postRepository.save(post);
    }

    public PostResponseDto edit(PostRequestDto postDto) {
        if(authService.isUserLoggedIn()){
            User commentUser = userRepository.findByUsername(postDto.getUserName())
                    .orElseThrow(() -> new redditUserNotFoundException(postDto.getUserName()));
            if(!authService.getCurrentUser().equals(commentUser)){
                throw new redditUserNotFoundException();
            }
        } else {
            throw new redditException("You need to Login!");
        }
        Post post = postRepository.findById(postDto.getPostId())
                .orElseThrow(() -> new redditException("Post not found!"));
        post.setDescription(postDto.getDescription());
        postRepository.save(post);
        return postToPostResponse(post);
    }

    public List<PostResponseDto> getUserPosts() {
        return authService.getCurrentUser().getPosts()
                .stream()
                .map(post -> PostResponseDto.builder().postId(post.getPostId()).build())
                .collect(toList());
    }
}
