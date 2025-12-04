package com.minelog.road.auth.service;

import com.minelog.road.auth.config.JwtProperties;
import com.minelog.road.auth.domain.AuthUser;
import com.minelog.shared.common.domain.User;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

  @Mock
  private JwtProperties jwtProperties;

  @InjectMocks
  private JwtService jwtService;

  private static final String SECRET_KEY = "testSecretKeyThatIsAtLeast32CharactersLongForHS256";
  private static final long EXPIRATION_MS = 3600000L; // 1 hour

  private AuthUser authUser;

  @BeforeEach
  void setUp() {
    lenient().when(jwtProperties.getSecretKey()).thenReturn(SECRET_KEY);
    when(jwtProperties.getExpirationMs()).thenReturn(EXPIRATION_MS);

    User user = User.builder()
        .id(1L)
        .username("testuser")
        .email("test@example.com")
        .password("password")
        .enabled(true)
        .build();

    authUser = new AuthUser(user);

    // Initialize the secret key manually since @PostConstruct won't run in unit tests
    SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    try {
      java.lang.reflect.Field field = JwtService.class.getDeclaredField("secretKey");
      field.setAccessible(true);
      field.set(jwtService, secretKey);
    } catch (Exception e) {
      throw new RuntimeException("Failed to set secretKey field", e);
    }
  }

  @Test
  void generateToken_WhenValidUserDetails_ReturnsValidToken() {
    // When
    String token = jwtService.generateToken(authUser);

    // Then
    assertThat(token).isNotNull();
    assertThat(token).isNotEmpty();
    assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts: header.payload.signature
  }

   @Test
   void generateToken_WhenCalledMultipleTimes_GeneratesDifferentTokens() throws InterruptedException {
     // When
     String token1 = jwtService.generateToken(authUser);
     Thread.sleep(1000);
     String token2 = jwtService.generateToken(authUser);

     // Then
     assertThat(token1).isNotEqualTo(token2); // Different issuedAt times
     assertThat(token1.split("\\.")).hasSize(3);
     assertThat(token2.split("\\.")).hasSize(3);
   }

  @Test
  void generateToken_WhenCalledWithDifferentUsers_GeneratesDifferentTokens() {
    // Given
    User user2 = User.builder()
        .id(2L)
        .username("user2")
        .email("user2@example.com")
        .password("password")
        .enabled(true)
        .build();
    AuthUser authUser2 = new AuthUser(user2);

    // When
    String token1 = jwtService.generateToken(authUser);
    String token2 = jwtService.generateToken(authUser2);

    // Then
    assertThat(token1).isNotEqualTo(token2);
  }

  @Test
  void getExpirationMs_ReturnsConfiguredValue() {
    // When
    long expirationMs = jwtService.getExpirationMs();

    // Then
    assertThat(expirationMs).isEqualTo(EXPIRATION_MS);
  }
}

