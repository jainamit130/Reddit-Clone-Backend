package com.amit.reddit.controller;

import com.amit.reddit.dto.LoginDto;
import com.amit.reddit.dto.RefreshTokenRequest;
import com.amit.reddit.dto.RegisterDto;
import com.amit.reddit.dto.ResponseDto;
import com.amit.reddit.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

@RestController
@RequestMapping("/reddit/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody RegisterDto register){
        return authService.signup(register);
    }

    @GetMapping("/activateAccount/{token}")
    public ResponseEntity<String> activateAccount(@PathVariable String token){
        authService.activateAccount(token);
        return new ResponseEntity<>("Account activated successfully", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseDto login(@RequestBody LoginDto loginDto) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
        return authService.login(loginDto);
    }

    @PostMapping("/refreshToken")
    public ResponseDto refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest){
        return authService.refreshToken(refreshTokenRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest){
        authService.logout(refreshTokenRequest);
        return new ResponseEntity<>("Logged out Successfully!",HttpStatus.OK);
    }
}
