package com.example.ZenDesk.service;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.ZenDesk.entity.AppUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    String secret;
    @Value("${jwt.exp}")
    Long exp;
    public String generateToken(AppUser user)
    {
        return Jwts.builder()
            .subject(user.getEmail())
            .claim("Role:",user.getRole())
            .claim("userId", user.getId())
            .claim("fullName", user.getFullName())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + exp))
            .signWith(getSecretKey())
            .compact();
    }

    SecretKey getSecretKey()
    {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Claims extractAllClaims(String token)
    {
        return Jwts.parser()
            .verifyWith(getSecretKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public <T> T extractClaim(String token,Function<Claims,T> resolver)
    {
        return resolver.apply(extractAllClaims(token));
    }

    public String extractUsername(String token)
    {
        return extractClaim(token,Claims::getSubject);
    }

    public boolean isTokenExpired(String token)
    {
        return extractClaim(token,Claims::getExpiration).before(new Date());
    }

    public boolean validateToken(String token,UserDetails user)
    {
        String userdata=extractUsername(token);
        return userdata.equals(user.getUsername()) && !isTokenExpired(token);
    }

}
