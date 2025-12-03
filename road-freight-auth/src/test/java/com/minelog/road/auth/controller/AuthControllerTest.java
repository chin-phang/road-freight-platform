package com.minelog.road.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minelog.road.auth.domain.AuthUser;
import com.minelog.road.auth.dto.LoginRequest;
import com.minelog.road.auth.service.JwtService;
import com.minelog.shared.common.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private AuthenticationManager authenticationManager;

  @MockBean
  private JwtService jwtService;

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
}

