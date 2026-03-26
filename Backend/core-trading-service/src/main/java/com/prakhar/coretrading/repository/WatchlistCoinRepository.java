package com.prakhar.coretrading.repository;

import com.prakhar.coretrading.entity.WatchlistCoin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WatchlistCoinRepository extends JpaRepository<WatchlistCoin, Long> {
    List<WatchlistCoin> findByUserId(Long userId);
    Optional<WatchlistCoin> findByUserIdAndCoinId(Long userId, String coinId);
    void deleteByUserIdAndCoinId(Long userId, String coinId);
}
