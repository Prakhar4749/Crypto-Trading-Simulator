package com.prakhar.auth.service;

import com.prakhar.auth.dto.request.SignupRequest;
import com.prakhar.auth.entity.User;
import com.prakhar.auth.enums.UserRole;
import com.prakhar.auth.feign.CoreTradingClient;
import com.prakhar.auth.mapper.UserMapper;
import com.prakhar.auth.repository.ForgotPasswordTokenRepository;
import com.prakhar.auth.repository.UserRepository;
import com.prakhar.auth.repository.VerificationCodeRepository;
import com.prakhar.auth.service.impl.AuthServiceImpl;
import com.prakhar.auth.utils.JwtProvider;
import com.prakhar.common.dto.ApiResponse;
import com.prakhar.common.dto.WalletDTO;
import com.prakhar.common.exception.BusinessException;
import com.prakhar.common.exception.DuplicateResourceException;
import com.prakhar.common.exception.UnauthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private KafkaTemplate<String, Object> kafkaTemplate;
  @Mock
  private TwoFactorOtpService twoFactorOtpService;
  @Mock
  private VerificationCodeRepository verificationCodeRepository;
  @Mock
  private ForgotPasswordTokenRepository forgotPasswordTokenRepository;
  @Mock
  private JwtProvider jwtProvider;
  @Mock
  private UserMapper userMapper;
  @Mock
  private CoreTradingClient coreTradingClient;
  
  @InjectMocks
  private AuthServiceImpl authService;

  @Test
  @DisplayName("Signup success — returns JWT")
  void signupSuccess() {
    when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(any())).thenReturn("encoded");
    when(userRepository.save(any())).thenAnswer(i -> {
        User u = i.getArgument(0);
        u.setId(1L);
        return u;
    });
    when(coreTradingClient.createWalletForUser(any()))
      .thenReturn(ApiResponse.success("ok", new WalletDTO()));
    when(jwtProvider.generateToken(any(), any(), any(), any()))
      .thenReturn("jwt-token");

    String token = authService.signup("Test User", "test@test.com", "1234567890", "password123");
    assertNotNull(token);
    assertEquals("jwt-token", token);
  }

  @Test
  @DisplayName("Signup fails — duplicate email")
  void signupDuplicateEmail() {
    when(userRepository.findByEmail("dup@test.com")).thenReturn(Optional.of(new User()));

    assertThrows(
      DuplicateResourceException.class,
      () -> authService.signup("Test", "dup@test.com", "1234567890", "pass")
    );
  }

  @Test
  @DisplayName("Login success")
  void signinSuccess() {
    User user = new User();
    user.setId(1L);
    user.setEmail("test@test.com");
    user.setPassword("encoded");
    user.setVerified(true);
    user.setActive(true);
    user.setRole(UserRole.ROLE_USER);

    when(userRepository.findByEmail("test@test.com"))
      .thenReturn(Optional.of(user));
    when(passwordEncoder.matches("password", "encoded"))
      .thenReturn(true);
    when(jwtProvider.generateToken(any(), any(), any(), any()))
      .thenReturn("jwt-token");

    var response = authService.signin("test@test.com", "password");
    assertEquals("jwt-token", response.get("jwt"));
  }

  @Test
  @DisplayName("Login fails — wrong password")
  void signinWrongPassword() {
    User user = new User();
    user.setEmail("test@test.com");
    user.setPassword("encoded");
    user.setVerified(true);
    user.setActive(true);

    when(userRepository.findByEmail(any()))
      .thenReturn(Optional.of(user));
    when(passwordEncoder.matches(any(), any()))
      .thenReturn(false);

    assertThrows(
      UnauthorizedException.class,
      () -> authService.signin("test@test.com", "wrongpass")
    );
  }

  @Test
  @DisplayName("Claim bonus success")
  void claimBonusSuccess() {
    User user = new User();
    user.setId(1L);
    user.setVerified(true);
    user.setSignupBonusAvailed(false);
    user.setBonusClaimToken("valid-token");
    user.setBonusClaimTokenExpiry(LocalDateTime.now().plusHours(1));

    when(userRepository.findByBonusClaimToken("valid-token"))
      .thenReturn(Optional.of(user));
    when(coreTradingClient.creditSignupBonus(any()))
      .thenReturn(ApiResponse.success("ok", new WalletDTO()));

    assertDoesNotThrow(() -> 
      authService.claimSignupBonus("valid-token"));
    assertTrue(user.isSignupBonusAvailed());
  }

  @Test
  @DisplayName("Claim bonus fails — already claimed")
  void claimBonusAlreadyClaimed() {
    User user = new User();
    user.setSignupBonusAvailed(true);
    user.setBonusClaimToken("token");
    user.setBonusClaimTokenExpiry(LocalDateTime.now().plusHours(1));

    when(userRepository.findByBonusClaimToken("token"))
      .thenReturn(Optional.of(user));

    assertThrows(
      BusinessException.class,
      () -> authService.claimSignupBonus("token")
    );
  }

  @Test
  @DisplayName("Claim bonus fails — expired token")
  void claimBonusExpiredToken() {
    User user = new User();
    user.setVerified(true);
    user.setSignupBonusAvailed(false);
    user.setBonusClaimToken("expired-token");
    user.setBonusClaimTokenExpiry(LocalDateTime.now().minusHours(1));

    when(userRepository.findByBonusClaimToken("expired-token"))
      .thenReturn(Optional.of(user));

    assertThrows(
      BusinessException.class,
      () -> authService.claimSignupBonus("expired-token")
    );
  }
}
