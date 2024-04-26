package com.amit.reddit.controller;

import com.amit.reddit.dto.CommentDto;
import com.amit.reddit.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/reddit/comments/")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/create")
    public ResponseEntity createComment(@RequestBody CommentDto commentDto){
        commentService.create(commentDto);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping("getPostComments")
    public ResponseEntity<List<CommentDto>> getAllPostComments(@RequestParam(name="postId") Long postId){
        return new ResponseEntity(commentService.getAllPostComments(postId),HttpStatus.OK);
    }

    @GetMapping("getUserComment")
    public ResponseEntity<List<CommentDto>> getAllUserComments(@RequestParam(name = "username") String username){
        return new ResponseEntity(commentService.getAllUserComments(username),HttpStatus.OK);
    }

    @PostMapping("deleteComment")
    public ResponseEntity deleteComment(@RequestParam(name = "postId") Long postId,@RequestParam(name="commentId") Long commentId){
        commentService.deleteComment(postId,commentId);
        return new ResponseEntity(HttpStatus.OK);

    }
}
