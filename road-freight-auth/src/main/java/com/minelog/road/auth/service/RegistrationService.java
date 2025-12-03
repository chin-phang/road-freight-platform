package com.minelog.road.auth.service;

import com.minelog.road.auth.domain.AuthUser;
import com.minelog.road.auth.dto.RegisterRequest;
import com.minelog.road.auth.dto.RegisterResponse;
import com.minelog.road.auth.repository.UserRepository;
import com.minelog.shared.common.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrationService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  @Transactional
  public RegisterResponse register(RegisterRequest request) {
    if (userRepository.findByEmail(request.email()).isPresent()) {
      throw new IllegalArgumentException("Email is already in use");
    }

    User user = User.builder()
        .username(request.username())
        .email(request.email())
        .password(passwordEncoder.encode(request.password()))
        .enabled(true)
        .build();

    try {
      User saved = userRepository.save(user);
      AuthUser authUser = new AuthUser(saved);
      String token = jwtService.generateToken(authUser);
      long expiresInMs = jwtService.getExpirationMs();

      return new RegisterResponse(saved.getId(), token, expiresInMs);
    } catch (DataIntegrityViolationException ex) {
      // In case of a race condition with unique constraints
      throw new IllegalArgumentException("User with given email or username already exists", ex);
    }
  }
}


