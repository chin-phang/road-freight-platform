package com.minelog.road.auth.service;

import com.minelog.road.auth.config.JwtProperties;
import com.minelog.road.auth.domain.AuthUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

  private final JwtProperties jwtProperties;
  private SecretKey secretKey;

  @PostConstruct
  void initSigningKey() {
    this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(UserDetails userDetails) {
    Date issuedAt = new Date();
    Date expiration = new Date(issuedAt.getTime() + jwtProperties.getExpirationMs());

    return Jwts.builder()
        .setSubject(userDetails.getUsername())
        .claim("uid", extractUserId(userDetails))
        .setIssuedAt(issuedAt)
        .setExpiration(expiration)
        .signWith(secretKey, SignatureAlgorithm.HS256)
        .compact();
  }

  private Long extractUserId(UserDetails userDetails) {
    if (userDetails instanceof AuthUser authUser) {
      return authUser.getUserId();
    }
    return null;
  }

  public long getExpirationMs() {
    return jwtProperties.getExpirationMs();
  }
}

