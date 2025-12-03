package com.minelog.road.auth.service;

import com.minelog.road.auth.domain.AuthUser;
import com.minelog.road.auth.repository.UserRepository;
import com.minelog.shared.common.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private CustomUserDetailsService userDetailsService;

  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
        .id(1L)
        .username("testuser")
        .email("test@example.com")
        .password("encodedPassword")
        .enabled(true)
        .build();
  }

  @Test
  void loadUserByUsername_WhenUserExists_ReturnsAuthUser() {
    // Given
    String email = "test@example.com";
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

    // When
    UserDetails result = userDetailsService.loadUserByUsername(email);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isInstanceOf(AuthUser.class);
    assertThat(result.getUsername()).isEqualTo("testuser");
    assertThat(result.getPassword()).isEqualTo("encodedPassword");
    assertThat(result.isEnabled()).isTrue();
  }

  @Test
  void loadUserByUsername_WhenUserDoesNotExist_ThrowsUsernameNotFoundException() {
    // Given
    String email = "nonexistent@example.com";
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    // When/Then
    assertThatThrownBy(() -> userDetailsService.loadUserByUsername(email))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessageContaining("User not found with email: " + email);
  }

  @Test
  void loadUserByUsername_WhenUserIsDisabled_ReturnsDisabledAuthUser() {
    // Given
    testUser.setEnabled(false);
    String email = "test@example.com";
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

    // When
    UserDetails result = userDetailsService.loadUserByUsername(email);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.isEnabled()).isFalse();
  }

  @Test
  void loadUserByUsername_VerifiesAccountStatusFlags() {
    // Given
    String email = "test@example.com";
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

    // When
    UserDetails result = userDetailsService.loadUserByUsername(email);

    // Then
    assertThat(result.isAccountNonExpired()).isTrue();
    assertThat(result.isAccountNonLocked()).isTrue();
    assertThat(result.isCredentialsNonExpired()).isTrue();
  }
}

