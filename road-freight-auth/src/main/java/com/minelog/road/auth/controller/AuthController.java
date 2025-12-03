package com.minelog.road.auth.controller;

import com.minelog.road.auth.dto.LoginRequest;
import com.minelog.road.auth.dto.LoginResponse;
import com.minelog.road.auth.dto.RegisterRequest;
import com.minelog.road.auth.dto.RegisterResponse;
import com.minelog.road.auth.service.JwtService;
import com.minelog.road.auth.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for authenticating users and issuing JWTs")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final RegistrationService registrationService;

  @PostMapping(
      value = "/login",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @Operation(
      summary = "Authenticate user and issue JWT",
      description = "Authenticates a user with email and password and returns a JWT access token.",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Authentication successful, JWT returned",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = LoginResponse.class)
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Invalid request payload",
              content = @Content
          ),
          @ApiResponse(
              responseCode = "401",
              description = "Authentication failed",
              content = @Content
          )
      }
  )
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.email(), request.password())
    );

    UserDetails principal = (UserDetails) authentication.getPrincipal();
    String token = jwtService.generateToken(principal);

    return ResponseEntity.ok(new LoginResponse(token, jwtService.getExpirationMs()));
  }

  @PostMapping(
      value = "/register",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @Operation(
      summary = "Register a new user and issue JWT",
      description = "Registers a new user account and returns a JWT access token for immediate use.",
      responses = {
          @ApiResponse(
              responseCode = "201",
              description = "User registered successfully, JWT returned",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = RegisterResponse.class)
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Invalid request payload or user already exists",
              content = @Content
          )
      }
  )
  public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
    RegisterResponse response = registrationService.register(request);
    return ResponseEntity.status(201).body(response);
  }
}

