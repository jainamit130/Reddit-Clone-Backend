package com.amit.reddit.controller;

import com.amit.reddit.dto.CommunityDto;
import com.amit.reddit.dto.PostRequestDto;
import com.amit.reddit.dto.PostResponseDto;
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
    public ResponseEntity<PostResponseDto> createPost(@RequestBody PostRequestDto postDto){
        return new ResponseEntity<PostResponseDto>(postService.create(postDto),HttpStatus.CREATED);
    }

    @GetMapping("getPost/{id}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable Long id){
        return new ResponseEntity(postService.getPost(id),HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<List<PostResponseDto>> getAllPosts(){
        return new ResponseEntity(postService.getAllPosts(),HttpStatus.OK);
    }

    @PutMapping("/edit")
    public ResponseEntity editPost(@RequestBody PostRequestDto postDto){
        return new ResponseEntity<PostResponseDto>(postService.edit(postDto),HttpStatus.CREATED);
    }

    @GetMapping("/community/{id}")
    public ResponseEntity<List<PostResponseDto>> getPostsByCommunity(@PathVariable Long id){
        return new ResponseEntity(postService.getPostsByCommunity(id),HttpStatus.OK);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<PostResponseDto>> getPostsByCommunity(@PathVariable String username){
        return new ResponseEntity(postService.getPostsByUsername(username),HttpStatus.OK);
    }

    @PostMapping("/deletePost/{postId}")
    public ResponseEntity deletePost(@PathVariable Long postId){
        postService.deletePost(postId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/getUserPosts")
    public ResponseEntity<List<PostResponseDto>> getUserPosts(){
        return new ResponseEntity<>(postService.getUserPosts(), HttpStatus.OK);
    }
}
