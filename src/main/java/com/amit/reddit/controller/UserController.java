package com.amit.reddit.controller;

import com.amit.reddit.dto.UserProfileDto;
import com.amit.reddit.model.Post;
import com.amit.reddit.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/reddit/user/")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable(name = "id") Long id){
        return new ResponseEntity<UserProfileDto>(userService.getUserProfileDetails(id), HttpStatus.OK);
    }


    @GetMapping("/getUserHistory")
    public ResponseEntity<List<Post>> getUserHistory(){
        return new ResponseEntity<>(userService.getUserHistory(),HttpStatus.OK);
    }

    @GetMapping("/clearHistory")
    public ResponseEntity clearHistory(){
        userService.clearRecentPostList();
        return new ResponseEntity(HttpStatus.OK);
    }
}
