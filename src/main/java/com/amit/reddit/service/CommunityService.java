package com.amit.reddit.service;

import com.amit.reddit.dto.CommunityDto;
import com.amit.reddit.dto.CommunitySearchDto;
import com.amit.reddit.exceptions.redditException;
import com.amit.reddit.mapper.CommunityMapper;
import com.amit.reddit.model.Community;
import com.amit.reddit.model.User;
import com.amit.reddit.repository.CommunityRepository;
import com.amit.reddit.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final CommunityMapper communityMapper;
    private final PostService postService;
    private final AuthService authService;

    @Transactional
    public CommunityDto create(CommunityDto communityDto){
        if(communityRepository.existsByCommunityNameIgnoreCase(communityDto.getCommunityName())){
            throw new redditException("Sorry, "+communityDto.getCommunityName()+" is taken. Try another.");
        }
        User user = authService.getCurrentUser();
        Community community = communityMapper.mapDtoToCommunity(communityDto);
        community.setCreatorUser(user);
        community.addMember(user);
        community.setCreationDate(Instant.now());
        CommunityDto responseCommunityDto = communityMapper.mapCommunityToDto(communityRepository.save(community));
        user.addCommunity(community);
        userRepository.save(user);
        responseCommunityDto.setUser(user);
        return responseCommunityDto;
    }

    public List<CommunityDto> getAll() {
        return communityRepository.findAll()
                .stream()
                .map(communityMapper::mapCommunityToDto)
                .collect(toList());
    }

    public List<CommunityDto> getUserCommunities() {
        return authService.getCurrentUser().getCommunities()
                .stream()
                .map(communityMapper::mapCommunityToDto)
                .collect(toList());
    }

    public CommunityDto getCommunity(Long id) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new redditException("No results found"));
        return communityMapper.mapCommunityToDto(community);
    }

    public CommunityDto getCommunityOfPost(Long postId) {
        Community community = communityRepository.findByPosts_PostId(postId)
                .orElseThrow(() -> new redditException("No results found"));
        CommunityDto communityDto = communityMapper.mapCommunityToDto(community);
        return communityDto;
    }

    public CommunityDto getCommunityWithPosts(Long id) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new redditException("No results found"));
        CommunityDto communityDto = communityMapper.mapCommunityToDto(community);
        communityDto.setPosts(community.getPosts().stream().map(post -> postService.postToPostResponse(post)).collect(Collectors.toList()));
        return communityDto;
    }

    @Transactional
    public CommunityDto joinCommunity(Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new redditException("No such community exists"));
        User currentUser = authService.getCurrentUser();
        List<Community> userCommunities =  currentUser.getCommunities();
        if(userCommunities.contains(community)){
            throw new redditException("User is already a member of "+community.getCommunityName());
        }
        community.addMember(currentUser);
        currentUser.addCommunity(community);
        userRepository.save(currentUser);
        return communityMapper.mapCommunityToDto(communityRepository.save(community));
    }

    @Transactional
    public CommunityDto leaveCommunity(Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new redditException("No such community exists"));
        User currentUser = authService.getCurrentUser();
        List<Community> userCommunities =  currentUser.getCommunities();
        if(!userCommunities.contains(community)){
            throw new redditException("You are not a member of "+community.getCommunityName());
        }
        community.removeUser();
        currentUser.leaveCommunity(community);
        userRepository.save(currentUser);
        return communityMapper.mapCommunityToDto(communityRepository.save(community));
    }

    public List<CommunitySearchDto> search(String prefix) {
        List<Community> communities = communityRepository.findAllByCommunityNameStartsWithIgnoreCase(prefix);
        return communities.stream()
                .map(community -> CommunitySearchDto.builder().communityName(community.getCommunityName()).communityId(community.getCommunityId()).numberOfMembers(community.getNumberOfMembers()).build())
                .collect(toList());
    }

    public List<CommunityDto> getAllSearchedCommunities(String searchQuery) {
        return communityRepository.findAllByDescriptionOrCommunityNameContains(searchQuery.toLowerCase())
                .stream()
                .map(community -> communityMapper.mapCommunityToDto(community))
                .collect(Collectors.toList());
    }
}
