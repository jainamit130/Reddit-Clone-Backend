package com.amit.reddit.controller;

import com.amit.reddit.dto.CommunityDto;
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

    @GetMapping("/get/{id}")
    public ResponseEntity<CommunityDto> getCommunity(@PathVariable Long id){
        return new ResponseEntity<>(communityService.getCommunity(id),HttpStatus.OK);
    }

    @GetMapping("/getAllCommunities")
    public ResponseEntity<List<CommunityDto>> getAllCommunities(){
        return new ResponseEntity<>(communityService.getAll(), HttpStatus.OK);
    }
}
