package com.chatapp.vartalaab.service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.nio.charset.Charset;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.chatapp.vartalaab.redisEntity.UserSessionDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private UserSessionService userSessionService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.token.expire.time}")
    private long jwtTokenExpiration;

    @Value("${jwt.token.refresh.threshold}")
    private long jwtTokenRefreshThreshold;

    public JwtService(UserSessionService userSessionService){
        this.userSessionService = userSessionService;
    }


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @SuppressWarnings("deprecation")
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(Base64.getEncoder().encodeToString(jwtSecret.getBytes())).build()
                //.setSigningKey(jwtSecret.getBytes(Charset.forName("UTF-8"))).build()
                .parseClaimsJws(token.replace("{", "").replace("}",""))
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        //checking if the token is not blackListed
        UserSessionDetails userSessionDetails = userSessionService.getUserSessionDetails(username)
                                                .orElseThrow(()-> new NoSuchElementException());
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token) && !userSessionDetails.isLoggedOut(token));
    }

    public String generateToken(String username){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    public boolean shouldRefreshToken(String token) {
        Date expiration = extractExpiration(token);
        return expiration.getTime() - System.currentTimeMillis() < jwtTokenRefreshThreshold;
    }

    public String refreshToken(String token) {
        Claims claims = extractAllClaims(token);
        String username = extractUsername(token);
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtTokenExpiration);

        return Jwts.builder()
        .setClaims(claims)
        .setSubject(username)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(SignatureAlgorithm.HS256, jwtSecret.getBytes(Charset.forName("UTF-8"))).compact();
    }

    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+jwtTokenExpiration))
                .signWith(SignatureAlgorithm.HS256, jwtSecret.getBytes(Charset.forName("UTF-8"))).compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
