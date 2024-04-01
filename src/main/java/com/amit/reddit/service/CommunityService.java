package com.amit.reddit.service;

import com.amit.reddit.dto.CommunityDto;
import com.amit.reddit.exceptions.redditException;
import com.amit.reddit.mapper.CommunityMapper;
import com.amit.reddit.model.Community;
import com.amit.reddit.repository.CommunityRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityMapper communityMapper;

    @Transactional
    public CommunityDto create(CommunityDto communityDto){
        if(communityRepository.existsByCommunityNameIgnoreCase(communityDto.getCommunityName())){
            throw new redditException("Sorry, "+communityDto.getCommunityName()+" is taken. Try another.");
        }
        Community community = communityRepository.save(communityMapper.mapDtoToCommunity(communityDto));
        communityDto.setCommunityId(community.getCommunityId());
        return communityDto;
    }

    @Transactional(readOnly = true)
    public List<CommunityDto> getAll() {
        return communityRepository.findAll()
                .stream()
                .map(communityMapper::mapCommunityToDto)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public CommunityDto getCommunity(Long id) {
        Optional<Community> community = communityRepository.findById(id);
        community.orElseThrow(() -> new redditException("No results found"));
        return community.map(communityMapper::mapCommunityToDto).get();
    }
}
