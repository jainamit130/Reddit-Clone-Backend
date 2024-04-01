package com.amit.reddit.controller;

import com.amit.reddit.dto.PostDto;
import com.amit.reddit.dto.PostResponseDto;
import com.amit.reddit.model.Post;
import com.amit.reddit.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/reddit/posts/")
public class PostController {

    private final PostService postService;

    @PostMapping("/create")
    public ResponseEntity<Post> createPost(@RequestBody PostDto postDto){
        return new ResponseEntity(postService.create(postDto),HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable Long id){
        return new ResponseEntity(postService.getPost(id),HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<List<PostResponseDto>> getAllPosts(){
        return new ResponseEntity(postService.getAllPosts(),HttpStatus.OK);
    }

    @GetMapping("/community/{id}")
    public ResponseEntity<List<PostResponseDto>> getPostsByCommunity(@PathVariable Long id){
        return new ResponseEntity(postService.getPostsByCommunity(id),HttpStatus.OK);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<PostResponseDto>> getPostsByCommunity(@PathVariable String username){
        return new ResponseEntity(postService.getPostsByUsername(username),HttpStatus.OK);
    }
}
