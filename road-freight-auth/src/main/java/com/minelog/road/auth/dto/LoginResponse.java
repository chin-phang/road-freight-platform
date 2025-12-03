package com.minelog.road.auth.dto;

public record LoginResponse(String token, long expiresInMs) {
}

