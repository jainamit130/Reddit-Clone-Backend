package com.amit.reddit.service;

import com.amit.reddit.dto.PostDto;
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
    private final VoteRepository voteRepository;
    private final AuthService authService;
    private final PostMapper postMapper;

    public Post create(PostDto postDto) {
        String communityName=postDto.getCommunityName();
        Community community = communityRepository.findByCommunityName(communityName)
                .orElseThrow(() -> new communityNotFoundException(communityName));

        User currentUser = authService.getCurrentUser();
        Post post=postMapper.mapDtoToPost(postDto,community,currentUser);
        post.setVote(1);//On creating the post the vote is set by defualt to 1
        postRepository.save(post);
        Vote defaultVote=Vote.builder().post(post).user(currentUser).voteType(VoteType.UPVOTE).build();
        voteRepository.save(defaultVote);
        return post;
    }

    @Transactional(readOnly = true)
    public PostResponseDto getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new redditException("Post not found!"));
        return postMapper.mapPostToDto(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> getAllPosts() {
        List<PostResponseDto> posts=postRepository.findAll()
                .stream()
                .map(postMapper::mapPostToDto)
                .collect(Collectors.toList());
        return posts;
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> getPostsByCommunity(Long id) {
        Community community=communityRepository.findById(id)
                .orElseThrow(() -> new communityNotFoundException(id.toString()));
        List<PostResponseDto> posts=postRepository.findAllByCommunity(community)
                .stream()
                .map(postMapper::mapPostToDto)
                .collect(Collectors.toList());
        return posts;
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> getPostsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new redditUserNotFoundException(username));
        List<PostResponseDto> posts=postRepository.findAllByUser(user)
                .stream()
                .map(postMapper::mapPostToDto)
                .collect(Collectors.toList());
        return posts;
    }
}
