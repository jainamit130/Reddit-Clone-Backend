package com.amit.reddit.service;

import com.amit.reddit.dto.LoginDto;
import com.amit.reddit.dto.RefreshTokenRequest;
import com.amit.reddit.dto.RegisterDto;
import com.amit.reddit.dto.ResponseDto;
import com.amit.reddit.exceptions.redditException;
import com.amit.reddit.exceptions.redditUserNotFoundException;
import com.amit.reddit.model.NotificationEmail;
import com.amit.reddit.model.Role;
import com.amit.reddit.model.User;
import com.amit.reddit.model.VerificationToken;
import com.amit.reddit.repository.UserRepository;
import com.amit.reddit.repository.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public ResponseEntity<String> signup(RegisterDto register){
        if(userRepository.existsByUsername(register.getUserName())){
            return new ResponseEntity<>("Username is taken!!", HttpStatus.BAD_REQUEST);
        }
        User user = new User();
        user.setUsername(register.getUserName());
        user.setEmail(register.getEmail());
        user.setPassword(passwordEncoder.encode(register.getPassword()));
        user.setRole(Role.USER);
        user.setCreationDate(Instant.now());
        user.setVerified(false);

        userRepository.save(user);
        String token = generateVerificationToken(user);
        NotificationEmail notificationEmail= new NotificationEmail("Reddit: Please Activate your Account", user.getEmail(), "Thank you for signing up to Reddit /n"+
                "Please click on the below link to activate your account: "+
                "http://localhost:8080/reddit/auth/activateAccount/"+token);
        mailService.sendMail(notificationEmail);
        return new ResponseEntity<>("User Registration Successful", HttpStatus.OK);
    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationTokenRepository.save(verificationToken);
        return token;
    }

    @Transactional
    public void activateAccount(String token) {
        Optional<VerificationToken> verificationToken=verificationTokenRepository.findByToken(token);;
        verificationToken.ifPresentOrElse((authenticationToken) -> {
            Optional<User> user=userRepository.findById(authenticationToken.getUser().getUserId());
            user.ifPresentOrElse((UserToVerify) -> {
                UserToVerify.setVerified(true);
                userRepository.save(UserToVerify);
            },() -> {throw new redditException("No such account found. Please signup to register your account");});
            }, () -> { throw new redditException("Invalid token"); });
    }

    public ResponseDto login(LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginDto.getUserName(),
                        loginDto.getPassword()));
        var user = getUserFromUsername(loginDto.getUserName());
        String token = jwtService.generateToken(user);
        return ResponseDto.builder()
                .authenticationToken(token)
                .userName(loginDto.getUserName())
                .refreshToken(refreshTokenService.generateRefreshToken().getRefreshToken())
                .expiresAt(jwtService.getExpirationTime())
                .build();
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        String username = SecurityContextHolder.
                getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new redditUserNotFoundException(username));
    }

    protected boolean isUserLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth!=null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetails;
    }

    private User getUserFromUsername(String username){
        var user =userRepository.findByUsername(username)
                .orElseThrow(() -> new redditUserNotFoundException(username));
        return user;
    }

    public ResponseDto refreshToken(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validate(refreshTokenRequest.getRefreshToken());
        String token = jwtService.generateToken(getUserFromUsername(refreshTokenRequest.getUsername()));
        return ResponseDto.builder()
                .authenticationToken(token)
                .userName(refreshTokenRequest.getUsername())
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .expiresAt(jwtService.getExpirationTime())
                .build();
    }

    public void logout(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
    }
}
