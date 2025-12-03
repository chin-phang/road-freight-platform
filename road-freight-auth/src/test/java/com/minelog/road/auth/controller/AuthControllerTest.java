package com.minelog.road.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minelog.road.auth.domain.AuthUser;
import com.minelog.road.auth.dto.LoginRequest;
import com.minelog.road.auth.dto.RegisterRequest;
import com.minelog.road.auth.service.JwtService;
import com.minelog.road.auth.service.RegistrationService;
import com.minelog.shared.common.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AuthControllerTest {

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    // Build MockMvc without security filters (matching original addFilters = false)
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .build();
  }

  @MockitoBean
  private AuthenticationManager authenticationManager;

  @MockitoBean
  private JwtService jwtService;

  @MockitoBean
  private RegistrationService registrationService;

  @Test
  void loginReturnsToken() throws Exception {
    LoginRequest request = new LoginRequest("driver@example.com", "password");
    User user = User.builder()
        .id(1L)
        .username("driver1")
        .email("driver@example.com")
        .password("secret")
        .enabled(true)
        .build();
    AuthUser authUser = new AuthUser(user);
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        authUser,
        null,
        authUser.getAuthorities()
    );

    when(authenticationManager.authenticate(any())).thenReturn(authentication);
    when(jwtService.generateToken(authUser)).thenReturn("jwt-token");
    when(jwtService.getExpirationMs()).thenReturn(3600000L);

    mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("jwt-token"))
        .andExpect(jsonPath("$.expiresInMs").value(3600000L));
  }

  @Test
  void registerCreatesUserAndReturnsToken() throws Exception {
    RegisterRequest request = new RegisterRequest("driver1", "driver@example.com", "password");

    when(registrationService.register(any()))
        .thenReturn(new com.minelog.road.auth.dto.RegisterResponse(1L, "jwt-token", 3600000L));

    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId").value(1L))
        .andExpect(jsonPath("$.token").value("jwt-token"))
        .andExpect(jsonPath("$.expiresInMs").value(3600000L));
  }
}

