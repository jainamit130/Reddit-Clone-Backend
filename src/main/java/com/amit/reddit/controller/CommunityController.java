package com.amit.reddit.controller;

import com.amit.reddit.dto.CommunityDto;
import com.amit.reddit.dto.CommunitySearchDto;
import com.amit.reddit.service.CommunityService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/reddit/community/")
public class CommunityController {

    private CommunityService communityService;

    @PostMapping("/create")
    public CommunityDto create(@RequestBody CommunityDto communityDto){
        return communityService.create(communityDto);
    }

    @GetMapping("/communitySearch/{startsWith}")
    public ResponseEntity<List<CommunitySearchDto>> communitySearch(@PathVariable String startsWith){
        System.out.println("Broo it comes here: "+startsWith);
        return new ResponseEntity<List<CommunitySearchDto>>(communityService.search(startsWith),HttpStatus.OK);
    }

    @PostMapping("/join/{communityId}")
    public CommunityDto join(@PathVariable Long communityId){
        return communityService.joinCommunity(communityId);
    }

    @PostMapping("/leave/{communityId}")
    public CommunityDto leave(@PathVariable Long communityId){
        return communityService.leaveCommunity(communityId);
    }

    @GetMapping("/getCommunityOfPost/{postId}")
    public ResponseEntity<CommunityDto> getCommunityOfPost(@PathVariable Long postId){
        return new ResponseEntity<CommunityDto>(communityService.getCommunityOfPost(postId),HttpStatus.OK);
    }

    @GetMapping("/getCommunity/{id}")
    public ResponseEntity<CommunityDto> getCommunity(@PathVariable Long id){
        return new ResponseEntity<>(communityService.getCommunity(id),HttpStatus.OK);
    }

    @GetMapping("/getCommunityWithPosts/{id}")
    public ResponseEntity<CommunityDto> getCommunityWithPosts(@PathVariable Long id){
        return new ResponseEntity<>(communityService.getCommunityWithPosts(id),HttpStatus.OK);
    }

    @GetMapping("/getAllCommunities")
    public ResponseEntity<List<CommunityDto>> getAllCommunities() {
        List<CommunityDto> communities = communityService.getAll();
        return new ResponseEntity<>(communities, HttpStatus.OK);
    }

    @GetMapping("/getUserCommunities")
    public ResponseEntity<List<CommunityDto>> getUserCommunities(){
        return new ResponseEntity<>(communityService.getUserCommunities(), HttpStatus.OK);
    }
}
