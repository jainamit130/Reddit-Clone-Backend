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
        return new ResponseEntity<CommentDto>(commentService.create(commentDto),HttpStatus.CREATED);
    }

    @PutMapping("/edit")
    public ResponseEntity editComment(@RequestBody CommentDto commentDto){
        return new ResponseEntity<CommentDto>(commentService.edit(commentDto),HttpStatus.CREATED);
    }

    @GetMapping("getPostComments/{postId}")
    public ResponseEntity<List<CommentDto>> getAllPostComments(@PathVariable(name="postId") Long postId,@RequestParam(name="repliesCount") Integer repliesCount){
        return new ResponseEntity(commentService.getAllPostComments(postId,null,repliesCount),HttpStatus.OK);
    }

    @GetMapping("getMoreReplies/{commentId}")
    public ResponseEntity<List<CommentDto>> getMoreReplies(@PathVariable(name="commentId") Long commentId,@RequestParam(name="postId") Long postId){
        return new ResponseEntity(commentService.getAllPostComments(postId,commentId,2),HttpStatus.OK);
    }

    @GetMapping("getUserCommentOnPost/{postId}")
    public ResponseEntity<List<CommentDto>> getAllUserCommentsOnPost(@PathVariable(name = "postId") Long postId){
        return new ResponseEntity(commentService.getAllUserCommentsOnPost(postId),HttpStatus.OK);
    }

    @GetMapping("getComment/{commentId}")
    public ResponseEntity<CommentDto> getComment(@RequestParam(name = "postId") Long postId,@PathVariable(name="commentId") Long commentId){
        return new ResponseEntity<CommentDto>(commentService.getCommentBasedOnId(postId,commentId),HttpStatus.OK);
    }

    @GetMapping("getUserComment")
    public ResponseEntity<List<CommentDto>> getAllUserComments(@RequestParam(name = "username") String username){
        return new ResponseEntity(commentService.getAllUserComments(username),HttpStatus.OK);
    }

    @PostMapping("deleteComment/{commentId}")
    public ResponseEntity deleteComment(@RequestParam(name = "postId") Long postId,@PathVariable(name="commentId") Long commentId){
        commentService.deleteComment(postId,commentId);
        return new ResponseEntity(HttpStatus.OK);
    }
}
