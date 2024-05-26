package com.amit.reddit.controller;

import com.amit.reddit.dto.CommentDto;
import com.amit.reddit.dto.CommunityDto;
import com.amit.reddit.dto.PostResponseDto;
import com.amit.reddit.service.CommentService;
import com.amit.reddit.service.CommunityService;
import com.amit.reddit.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/reddit/search")
public class SearchController {

    private final PostService postService;
    private final CommentService commentService;
    private final CommunityService communityService;

    @GetMapping("/posts")
    public ResponseEntity<List<PostResponseDto>> redditSearchPosts(@RequestParam(name = "q") String searchQuery){
        return new ResponseEntity(postService.getAllSearchedPosts(searchQuery), HttpStatus.OK);
    }

    @GetMapping("/comments")
    public ResponseEntity<List<CommentDto>> redditSearchComments(@RequestParam(name = "q") String searchQuery){
        return new ResponseEntity(commentService.getAllSearchedComments(searchQuery), HttpStatus.OK);
    }

    @GetMapping("/communities")
    public ResponseEntity<List<CommunityDto>> redditSearchCommunities(@RequestParam(name = "q") String searchQuery){
        return new ResponseEntity(communityService.getAllSearchedCommunities(searchQuery), HttpStatus.OK);
    }
}
