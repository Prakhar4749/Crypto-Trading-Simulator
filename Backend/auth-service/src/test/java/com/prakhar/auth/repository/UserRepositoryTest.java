package com.prakhar.auth.repository;

import com.prakhar.auth.entity.User;
import com.prakhar.auth.enums.KycStatus;
import com.prakhar.auth.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("Should save and find user by email")
  void shouldSaveAndFindByEmail() {
    User user = createTestUser("test@test.com");
    userRepository.save(user);
    Optional<User> found = userRepository.findByEmail("test@test.com");
    assertTrue(found.isPresent());
    assertEquals("test@test.com", found.get().getEmail());
  }

  @Test
  @DisplayName("Should return empty for non-existent email")
  void shouldReturnEmptyForUnknownEmail() {
    Optional<User> found = userRepository.findByEmail("notfound@test.com");
    assertTrue(found.isEmpty());
  }

  @Test
  @DisplayName("Should check email exists")
  void shouldCheckEmailExists() {
    User user = createTestUser("exists@test.com");
    userRepository.save(user);
    assertTrue(userRepository.existsByEmail("exists@test.com"));
    assertFalse(userRepository.existsByEmail("nope@test.com"));
  }

  @Test
  @DisplayName("Should find by bonus claim token")
  void shouldFindByBonusClaimToken() {
    User user = createTestUser("tok@test.com");
    user.setBonusClaimToken("test-token-123");
    user.setBonusClaimTokenExpiry(LocalDateTime.now().plusHours(24));
    userRepository.save(user);
    Optional<User> found = userRepository.findByBonusClaimToken("test-token-123");
    assertTrue(found.isPresent());
  }

  private User createTestUser(String email) {
    User user = new User();
    user.setFullName("Test User");
    user.setEmail(email);
    user.setPassword("encoded-password");
    user.setRole(UserRole.ROLE_USER);
    user.setKycStatus(KycStatus.NOT_STARTED);
    user.setActive(true);
    return user;
  }
}
