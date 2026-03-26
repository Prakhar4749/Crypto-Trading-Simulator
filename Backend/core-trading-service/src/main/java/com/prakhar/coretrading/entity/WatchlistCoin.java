package com.prakhar.coretrading.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "watchlist_coins")
public class WatchlistCoin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "coin_id", nullable = false)
    private String coinId;

    public WatchlistCoin() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getCoinId() { return coinId; }
    public void setCoinId(String coinId) { this.coinId = coinId; }
}
