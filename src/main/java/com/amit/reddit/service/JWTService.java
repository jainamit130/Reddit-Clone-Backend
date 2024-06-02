package com.amit.reddit.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
//@ConfigurationProperties(prefix = "reddit.jwt")
public class JWTService {

    private final String privateKey="3BeunKCYFsLqESTSuHgRQKtsXmBy5scuegS2DZc7YcTm7LPpt98gz78r5Zy2gGAS9JA66mjWRXXoA7scwDwoPGPbzdJ3neLJR4S8XnbPgUg4Ln2BUFSmSJFLoGYCjRHZhEKuNwyPE4Gbs4BumZTS9P2z4UaWkMCUJcjABFR3mwb9g6fKjkd7EaJgUYMZnNen7T7foG9nWzUnzH77CamJKxNUYD2uGuxz6oB5aaXPYCLWYHpNhQBHXLkTqmu7zgHD";
    private final Integer expiration=1200000;

    public String extractUserName(String token) {
        return extractClaim(token,Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims ,T> claimsResolver) {
        final Claims claims = extractAllClaims(token).getPayload();
        return claimsResolver.apply(claims);
    }

    private Jws<Claims> extractAllClaims(String token){
        Jws<Claims> claims = Jwts
                .parser().verifyWith(getSignInKey()).build()
                .parseSignedClaims(token);
        return claims;
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final  String username =extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(),userDetails);
    }
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails){
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(Date.from(getExpirationTime()))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    public Instant getExpirationTime(){
        return Instant.now().plusMillis(expiration);
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(privateKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
