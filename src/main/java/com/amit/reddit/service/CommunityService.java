package com.amit.reddit.service;

import com.amit.reddit.dto.CommunityDto;
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
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final CommunityMapper communityMapper;
    private final AuthService authService;

    @Transactional
    public CommunityDto create(CommunityDto communityDto){
        if(communityRepository.existsByCommunityNameIgnoreCase(communityDto.getCommunityName())){
            throw new redditException("Sorry, "+communityDto.getCommunityName()+" is taken. Try another.");
        }
        User user = authService.getCurrentUser();
        Community community = communityMapper.mapDtoToCommunity(communityDto);
        community.addMember(user);
        community.setCreatorUserId(user.getUserId());
        community.setNumberOfPosts(0);
        community.setCreationDate(Instant.now());
        CommunityDto responseCommunityDto = communityMapper.mapCommunityToDto(communityRepository.save(community));
        user.addCommunity(community);
        userRepository.save(user);
        responseCommunityDto.setUser(user);
        return responseCommunityDto;
    }

    @Transactional(readOnly = true)
    public List<CommunityDto> getAll() {
        return communityRepository.findAll()
                .stream()
                .map(communityMapper::mapCommunityToDto)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public List<CommunityDto> getUserCommunities() {
        return authService.getCurrentUser().getCommunities()
                .stream()
                .map(communityMapper::mapCommunityToDto)
                .collect(toList());
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public CommunityDto getCommunityWithPosts(Long id) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new redditException("No results found"));
        CommunityDto communityDto = communityMapper.mapCommunityToDto(community);
        communityDto.setPosts(community.getPosts());
        return communityDto;
    }

    @Transactional
    public CommunityDto joinCommunity(CommunityDto communityDto) {
        Community community = communityRepository.findById(communityDto.getCommunityId())
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
    public CommunityDto leaveCommunity(CommunityDto communityDto) {
        Community community = communityRepository.findById(communityDto.getCommunityId())
                .orElseThrow(() -> new redditException("No such community exists"));
        User currentUser = authService.getCurrentUser();
        List<Community> userCommunities =  currentUser.getCommunities();
        if(!userCommunities.contains(community)){
            throw new redditException("You are not a member of "+community.getCommunityName());
        }
        community.removeUser(currentUser);
        currentUser.leaveCommunity(community);
        userRepository.save(currentUser);
        return communityMapper.mapCommunityToDto(communityRepository.save(community));
    }
}
