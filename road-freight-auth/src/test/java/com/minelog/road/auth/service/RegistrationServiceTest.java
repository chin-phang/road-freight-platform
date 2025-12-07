package com.minelog.road.auth.service;

import com.minelog.road.auth.domain.AuthUser;
import com.minelog.road.auth.dto.RegisterRequest;
import com.minelog.road.auth.dto.RegisterResponse;
import com.minelog.road.auth.repository.UserRepository;
import com.minelog.shared.common.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtService jwtService;

  @InjectMocks
  private RegistrationService registrationService;

  private RegisterRequest validRequest;
  private User savedUser;
  private static final String ENCODED_PASSWORD = "$2a$10$encodedPasswordHash";
  private static final String JWT_TOKEN = "test-jwt-token";
  private static final long EXPIRATION_MS = 3600000L;

  @BeforeEach
  void setUp() {
    validRequest = new RegisterRequest("testuser", "test@example.com", "password123");
    savedUser = User.builder()
        .id(1L)
        .username("testuser")
        .email("test@example.com")
        .password(ENCODED_PASSWORD)
        .enabled(true)
        .build();
  }

  @Test
  void register_WhenValidRequest_ReturnsRegisterResponse() {
    when(userRepository.findByEmail(validRequest.email())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(validRequest.password())).thenReturn(ENCODED_PASSWORD);
    when(userRepository.save(any(User.class))).thenReturn(savedUser);
    when(jwtService.generateToken(any(AuthUser.class))).thenReturn(JWT_TOKEN);
    when(jwtService.getExpirationMs()).thenReturn(EXPIRATION_MS);

    RegisterResponse response = registrationService.register(validRequest);

    assertThat(response).isNotNull();
    assertThat(response.userId()).isEqualTo(1L);
    assertThat(response.token()).isEqualTo(JWT_TOKEN);
    assertThat(response.expiresInMs()).isEqualTo(EXPIRATION_MS);

    verify(userRepository).findByEmail(validRequest.email());
    verify(passwordEncoder).encode(validRequest.password());
    verify(userRepository).save(any(User.class));
    verify(jwtService).generateToken(any(AuthUser.class));
    verify(jwtService).getExpirationMs();
  }

  @Test
  void register_WhenValidRequest_EncodesPassword() {
    when(userRepository.findByEmail(validRequest.email())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(validRequest.password())).thenReturn(ENCODED_PASSWORD);
    when(userRepository.save(any(User.class))).thenReturn(savedUser);
    when(jwtService.generateToken(any(AuthUser.class))).thenReturn(JWT_TOKEN);
    when(jwtService.getExpirationMs()).thenReturn(EXPIRATION_MS);

    registrationService.register(validRequest);

    verify(passwordEncoder).encode(validRequest.password());
  }

  @Test
  void register_WhenValidRequest_CreatesEnabledUser() {
    when(userRepository.findByEmail(validRequest.email())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(validRequest.password())).thenReturn(ENCODED_PASSWORD);
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
      User user = invocation.getArgument(0);
      assertThat(user.getEnabled()).isTrue();
      return savedUser;
    });
    when(jwtService.generateToken(any(AuthUser.class))).thenReturn(JWT_TOKEN);
    when(jwtService.getExpirationMs()).thenReturn(EXPIRATION_MS);

    registrationService.register(validRequest);

    verify(userRepository).save(any(User.class));
  }

  @Test
  void register_WhenEmailAlreadyExists_ThrowsIllegalArgumentException() {
    User existingUser = User.builder()
        .id(2L)
        .username("existinguser")
        .email(validRequest.email())
        .password("password")
        .enabled(true)
        .build();
    when(userRepository.findByEmail(validRequest.email())).thenReturn(Optional.of(existingUser));

    assertThatThrownBy(() -> registrationService.register(validRequest))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Email is already in use");

    verify(userRepository, never()).save(any(User.class));
    verify(passwordEncoder, never()).encode(anyString());
    verify(jwtService, never()).generateToken(any(AuthUser.class));
  }

  @Test
  void register_WhenDataIntegrityViolation_ThrowsIllegalArgumentException() {
    when(userRepository.findByEmail(validRequest.email())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(validRequest.password())).thenReturn(ENCODED_PASSWORD);
    when(userRepository.save(any(User.class)))
        .thenThrow(new DataIntegrityViolationException("Unique constraint violation"));

    assertThatThrownBy(() -> registrationService.register(validRequest))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("User with given email or username already exists")
        .hasCauseInstanceOf(DataIntegrityViolationException.class);

    verify(userRepository).save(any(User.class));
    verify(jwtService, never()).generateToken(any(AuthUser.class));
  }

  @Test
  void register_WhenValidRequest_GeneratesJwtToken() {
    when(userRepository.findByEmail(validRequest.email())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(validRequest.password())).thenReturn(ENCODED_PASSWORD);
    when(userRepository.save(any(User.class))).thenReturn(savedUser);
    when(jwtService.generateToken(any(AuthUser.class))).thenReturn(JWT_TOKEN);
    when(jwtService.getExpirationMs()).thenReturn(EXPIRATION_MS);

    registrationService.register(validRequest);

    verify(jwtService).generateToken(any(AuthUser.class));
  }

  @Test
  void register_WhenValidRequest_SetsCorrectUserFields() {
    when(userRepository.findByEmail(validRequest.email())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(validRequest.password())).thenReturn(ENCODED_PASSWORD);
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
      User user = invocation.getArgument(0);
      assertThat(user.getUsername()).isEqualTo(validRequest.username());
      assertThat(user.getEmail()).isEqualTo(validRequest.email());
      assertThat(user.getPassword()).isEqualTo(ENCODED_PASSWORD);
      assertThat(user.getEnabled()).isTrue();
      return savedUser;
    });
    when(jwtService.generateToken(any(AuthUser.class))).thenReturn(JWT_TOKEN);
    when(jwtService.getExpirationMs()).thenReturn(EXPIRATION_MS);

    registrationService.register(validRequest);

    verify(userRepository).save(any(User.class));
  }
}

