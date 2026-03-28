package com.prakhar.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prakhar.auth.dto.request.SignupRequest;
import com.prakhar.auth.mapper.UserMapper;
import com.prakhar.auth.repository.UserRepository;
import com.prakhar.auth.service.AuthService;
import com.prakhar.auth.service.GoogleAuthService;
import com.prakhar.common.dto.WalletDTO;
import com.prakhar.common.exception.BusinessException;
import com.prakhar.common.exception.DuplicateResourceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AuthController Tests")
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  
  @MockBean
  private AuthService authService;
  @MockBean
  private GoogleAuthService googleAuthService;
  @MockBean
  private UserRepository userRepository;
  @MockBean
  private UserMapper userMapper;

  @Test
  @DisplayName("POST /auth/signup — 200 OK")
  void signupReturns200() throws Exception {
    SignupRequest req = new SignupRequest();
    req.setFullName("Test User");
    req.setEmail("test@test.com");
    req.setMobile("9876543210");
    req.setPassword("password123");

    when(authService.signup(anyString(), anyString(), anyString(), anyString()))
      .thenReturn("jwt-token");

    mockMvc.perform(post("/auth/signup")
      .header("X-Internal-Api-Key", "test-internal-key")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(req)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(true))
      .andExpect(jsonPath("$.data.jwt").value("jwt-token"));
  }

  @Test
  @DisplayName("POST /auth/signup — 409 Conflict for duplicate email")
  void signupDuplicateReturns409() throws Exception {
    when(authService.signup(anyString(), anyString(), anyString(), anyString()))
      .thenThrow(new DuplicateResourceException("Email already exists"));

    SignupRequest req = new SignupRequest();
    req.setFullName("Test");
    req.setEmail("dup@test.com");
    req.setMobile("9876543210");
    req.setPassword("pass123");

    mockMvc.perform(post("/auth/signup")
      .header("X-Internal-Api-Key", "test-internal-key")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(req)))
      .andExpect(status().isConflict())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(jsonPath("$.error.code").value("DUPLICATE_RESOURCE"));
  }

  @Test
  @DisplayName("POST /auth/signup — 400 for invalid email")
  void signupInvalidEmailReturns400() throws Exception {
    mockMvc.perform(post("/auth/signup")
      .header("X-Internal-Api-Key", "test-internal-key")
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\"email\":\"not-an-email\",\"password\":\"pass\"}"))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.error.details").isArray());
  }

  @Test
  @DisplayName("POST /auth/claim-bonus — 200 on valid token")
  void claimBonusSuccess() throws Exception {
    when(authService.claimSignupBonus("valid-token"))
      .thenReturn(new WalletDTO());

    mockMvc.perform(post("/auth/claim-bonus")
        .header("X-Internal-Api-Key", "test-internal-key")
        .param("token", "valid-token"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(true));
  }

  @Test
  @DisplayName("POST /auth/claim-bonus — 422 for already claimed")
  void claimBonusAlreadyClaimedReturns422() throws Exception {
    when(authService.claimSignupBonus(anyString()))
      .thenThrow(new BusinessException("Bonus already claimed"));

    mockMvc.perform(post("/auth/claim-bonus")
        .header("X-Internal-Api-Key", "test-internal-key")
        .param("token", "used-token"))
      .andExpect(status().isUnprocessableEntity())
      .andExpect(jsonPath("$.error.code").value("BUSINESS_RULE_VIOLATION"));
  }
}
