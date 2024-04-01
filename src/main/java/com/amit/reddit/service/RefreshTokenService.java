package com.amit.reddit.service;

import com.amit.reddit.model.RefreshToken;
import com.amit.reddit.exceptions.redditException;
import com.amit.reddit.repository.RefreshTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    public RefreshToken generateRefreshToken(){
        RefreshToken refreshToken= new RefreshToken();
        refreshToken.setRefreshToken(UUID.randomUUID().toString());
        refreshToken.setCreationDate(Instant.now());
        return refreshTokenRepository.save(refreshToken);
    }

    public void validate(String refreshToken){
        refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new redditException("Invalid refresh token! Please try logging in."));
    }

    public void deleteRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteByRefreshToken(refreshToken);
    }
}
