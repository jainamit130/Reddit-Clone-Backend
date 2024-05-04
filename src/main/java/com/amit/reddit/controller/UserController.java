package com.amit.reddit.controller;

import com.amit.reddit.dto.UserProfileDto;
import com.amit.reddit.service.CommentService;
import com.amit.reddit.service.PostService;
import com.amit.reddit.service.UserService;
import com.amit.reddit.service.VoteService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/reddit/user/")
public class UserController {

    private final UserService userService;

    @GetMapping("/")
    public ResponseEntity<UserProfileDto> getUserProfile(){
        return new ResponseEntity<UserProfileDto>(userService.getUserProfileDetails(), HttpStatus.OK);
    }
}
