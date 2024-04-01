package com.amit.reddit.controller;

import com.amit.reddit.dto.VoteDto;
import com.amit.reddit.service.VoteService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/reddit/vote/")
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public ResponseEntity vote(@RequestBody VoteDto voteDto){
        voteService.vote(voteDto);
        return new ResponseEntity(HttpStatus.OK);
    }
}
