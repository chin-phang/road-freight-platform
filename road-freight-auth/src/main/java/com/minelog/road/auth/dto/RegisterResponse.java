package com.minelog.road.auth.dto;

public record RegisterResponse(
    Long userId,
    String token,
    long expiresInMs
) {
}


