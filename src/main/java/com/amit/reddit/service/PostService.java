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
import com.amit.reddit.repository.VoteRepository;
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
public class PostService {
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
        postRepository.save(post);
        community.addPost(post);
        communityRepository.save(community);
        voteService.saveDefaultVote(post);
        PostResponseDto postResponse = postToPostResponse(post);
        return postResponse;
    }

    @Transactional(readOnly = true)
    public PostResponseDto getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new redditException("Post not found!"));
        PostResponseDto postResponse = postToPostResponse(post);
        return postResponse;
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> getAllPosts() {
        List<PostResponseDto> posts=postRepository.findAll()
                .stream()
                .map(post -> {
                    return postToPostResponse(post);
                })
                .collect(Collectors.toList());
        return posts;
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> getPostsByCommunity(Long id) {
        Community community=communityRepository.findById(id)
                .orElseThrow(() -> new communityNotFoundException(id.toString()));
        List<PostResponseDto> posts=postRepository.findAllByCommunity(community)
                .stream()
                .map(post -> {
                    return postToPostResponse(post);
                })
                .collect(Collectors.toList());
        return posts;
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> getPostsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new redditUserNotFoundException(username));
        List<PostResponseDto> posts=postRepository.findAllByUser(user)
                .stream()
                .map(post -> {
                    return postToPostResponse(post);
                })
                .collect(Collectors.toList());
        return posts;
    }

    public PostResponseDto postToPostResponse(Post post) {
        PostResponseDto postResponse = postMapper.mapPostToDto(post);
        postResponse.setCurrentVote(voteService.getUserVote(post,null));
        return postResponse;
    }

    public List<PostResponseDto> getAllSearchedPosts(String searchQuery){
        return postRepository.findAllByDescriptionOrPostNameContains(searchQuery.toLowerCase())
                .stream()
                .map(post -> {
                    return postToPostResponse(post);
                })
                .collect(Collectors.toList());
    }
}
