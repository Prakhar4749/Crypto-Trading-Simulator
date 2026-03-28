package com.prakhar.coretrading.repository;

import com.prakhar.coretrading.entity.Wallet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("WalletRepository Tests")
class WalletRepositoryTest {

  @Autowired
  private WalletRepository walletRepository;

  @Test
  @DisplayName("Should save and find wallet by userId")
  void shouldSaveAndFindByUserId() {
    Wallet wallet = new Wallet();
    wallet.setUserId(1L);
    wallet.setBalance(new BigDecimal("1000.00"));
    walletRepository.save(wallet);

    Optional<Wallet> found = walletRepository.findByUserId(1L);
    assertTrue(found.isPresent());
    assertEquals(0, new BigDecimal("1000.00").compareTo(found.get().getBalance()));
  }

  @Test
  @DisplayName("Should check if wallet exists by userId")
  void shouldCheckExistsByUserId() {
    Wallet wallet = new Wallet();
    wallet.setUserId(2L);
    walletRepository.save(wallet);

    assertTrue(walletRepository.existsByUserId(2L));
    assertFalse(walletRepository.existsByUserId(99L));
  }

  @Test
  @DisplayName("Should find for update")
  void shouldFindByUserIdForUpdate() {
    Wallet wallet = new Wallet();
    wallet.setUserId(3L);
    walletRepository.save(wallet);

    Optional<Wallet> found = walletRepository.findByUserIdForUpdate(3L);
    assertTrue(found.isPresent());
    assertEquals(3L, found.get().getUserId());
  }
}
