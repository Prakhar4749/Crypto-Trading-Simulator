# Database Schema Design
## Project Name: CoinDesk Simulator
**Date:** March 12, 2026  
**Document Version:** 5.0

---

## 1. Overview
This document outlines the relational database schema for the CoinDesk Simulator, based strictly on the backend entity definitions. The schema is organized into logical domains corresponding to the microservices: Authentication (`auth-service`) and Core Trading (`core-trading-service`).

---

## 2. Authentication Service Schema (`auth-service`)
These tables handle user identity, security, and verification processes.

### 2.1 `users`
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGSERIAL` | `PRIMARY KEY` | Unique identifier for the user. |
| `full_name` | `VARCHAR(255)` | `NULL` | User's full name. |
| `email` | `VARCHAR(255)` | `UNIQUE, NOT NULL` | User's email address. |
| `mobile` | `VARCHAR(20)` | `NULL` | User's mobile phone number. |
| `password` | `VARCHAR(255)` | `NOT NULL` | Hashed password. |
| `role` | `VARCHAR(50)` | `NULL` | User role (e.g., USER, ADMIN). |
| `is_verified` | `BOOLEAN` | `DEFAULT FALSE` | Indicates if the account is email-verified. |
| `is_two_factor_enabled` | `BOOLEAN` | `DEFAULT FALSE` | Status of Two-Factor Authentication. |

### 2.2 `two_factor_otp`
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `VARCHAR(255)` | `PRIMARY KEY` | Unique string identifier. |
| `user_id` | `BIGINT` | `NULL` | References the user requesting 2FA. |
| `otp` | `VARCHAR(10)` | `NULL` | The One-Time Password. |
| `jwt` | `TEXT` | `NULL` | Associated JWT token. |
| `expiry_time` | `TIMESTAMP` | `NULL` | Expiration timestamp. |

### 2.3 `forgot_password_tokens`
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `VARCHAR(255)` | `PRIMARY KEY` | Unique string identifier. |
| `user_id` | `BIGINT` | `NULL` | References the user. |
| `otp` | `VARCHAR(10)` | `NULL` | The recovery OTP. |
| `email` | `VARCHAR(255)` | `NULL` | User's email address. |
| `type` | `VARCHAR(50)` | `NULL` | Enum: `VerificationType`. |
| `expiry_time` | `TIMESTAMP` | `NULL` | Expiration timestamp. |

### 2.4 `verification_codes`
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGSERIAL` | `PRIMARY KEY` | Unique auto-incrementing ID. |
| `user_id` | `BIGINT` | `NULL` | References the user. |
| `email` | `VARCHAR(255)` | `NULL` | User's email address. |
| `otp` | `VARCHAR(10)` | `NULL` | The verification OTP. |
| `type` | `VARCHAR(50)` | `NULL` | Enum: `VerificationType`. |
| `expiry_time` | `TIMESTAMP` | `NULL` | Expiration timestamp. |

---

## 3. Core Trading Service Schema (`core-trading-service`)
These tables manage virtual funds, trading mechanics, portfolio tracking, and withdrawal requests.

### 3.1 `wallets`
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGSERIAL` | `PRIMARY KEY` | Unique wallet identifier. |
| `user_id` | `BIGINT` | `UNIQUE, NOT NULL` | Owner of the wallet. |
| `balance` | `DECIMAL(19,4)`| `NOT NULL, DEFAULT 0`| Current virtual balance. |

### 3.2 `wallet_transactions`
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGSERIAL` | `PRIMARY KEY` | Unique transaction ID. |
| `wallet_id` | `BIGINT` | `NOT NULL` | References `wallets(id)`. |
| `type` | `VARCHAR(50)` | `NOT NULL` | Enum: `WalletTransactionType`. |
| `date` | `TIMESTAMP` | `NOT NULL` | Execution timestamp. |
| `transfer_id` | `VARCHAR(255)` | `NULL` | ID linking related transfers. |
| `purpose` | `VARCHAR(255)` | `NULL` | Description/purpose of transaction. |
| `amount` | `DECIMAL(19,4)`| `NOT NULL` | Amount transacted. |

### 3.3 `asset`
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGSERIAL` | `PRIMARY KEY` | Unique asset ID. |
| `user_id` | `BIGINT` | `NOT NULL` | Owner of the asset. |
| `coin_id` | `VARCHAR(255)` | `NOT NULL` | External coin identifier. |
| `quantity` | `DOUBLE PRECISION`| `NOT NULL` | Amount owned. |
| `buy_price` | `DOUBLE PRECISION`| `NOT NULL` | Average acquisition price. |

*(Note: Contains a unique table constraint on `(user_id, coin_id)` to prevent duplicate asset rows per coin for a single user).*

### 3.4 `orders`
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGSERIAL` | `PRIMARY KEY` | Unique order ID. |
| `user_id` | `BIGINT` | `NOT NULL` | User placing the order. |
| `order_type` | `VARCHAR(50)` | `NOT NULL` | e.g., BUY, SELL. |
| `price` | `DECIMAL(19,4)`| `NOT NULL` | Desired execution price. |
| `timestamp` | `TIMESTAMP` | `NOT NULL` | Time of placement. |
| `status` | `VARCHAR(50)` | `NOT NULL` | e.g., PENDING, FILLED, CANCELLED. |
| `coin_id` | `VARCHAR(255)` | `NOT NULL` | External coin identifier. |
| `quantity` | `DOUBLE PRECISION`| `NOT NULL` | Amount to trade. |
| `buy_price` | `DOUBLE PRECISION`| `NULL` | Execution buy price. |
| `sell_price` | `DOUBLE PRECISION`| `NULL` | Execution sell price. |

### 3.5 `payment_details`
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGSERIAL` | `PRIMARY KEY` | Unique record ID. |
| `account_number` | `VARCHAR(255)` | `NOT NULL` | Linked bank account number. |
| `account_holder_name`| `VARCHAR(255)`| `NOT NULL` | Name on the account. |
| `ifsc` | `VARCHAR(50)` | `NOT NULL` | Bank IFSC/Routing code. |
| `bank_name` | `VARCHAR(255)` | `NOT NULL` | Name of the institution. |
| `user_id` | `BIGINT` | `UNIQUE, NOT NULL` | User owning these details. |

### 3.6 `withdrawals`
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGSERIAL` | `PRIMARY KEY` | Unique withdrawal ID. |
| `user_id` | `BIGINT` | `NOT NULL` | User requesting withdrawal. |
| `email` | `VARCHAR(255)` | `NOT NULL` | Contact email for request. |
| `amount` | `DECIMAL(19,4)`| `NOT NULL` | Requested amount. |
| `status` | `VARCHAR(50)` | `NOT NULL` | Enum: `WithdrawalStatus`. |
| `date` | `TIMESTAMP` | `NOT NULL` | Date of request. |

### 3.7 `watchlist_coins`
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGSERIAL` | `PRIMARY KEY` | Unique record ID. |
| `user_id` | `BIGINT` | `NOT NULL` | Owner of the watchlist. |
| `coin_id` | `VARCHAR(255)` | `NOT NULL` | External coin identifier. |

---

## 4. Foreign Key Relationships (Implicit)
While JPA manages entity relationships, the database implicitly maps the following logically:
* `user_id` in `two_factor_otp`, `forgot_password_tokens`, `verification_codes`, `wallets`, `asset`, `orders`, `payment_details`, `withdrawals`, and `watchlist_coins` logically references `users(id)`.
* `wallet_id` in `wallet_transactions` logically references `wallets(id)`.

---

## 👨‍💻 Developer
**Prakhar Sakhare**
* **Role:** Backend Developer
* **Email:** prakharsakhare2226@gmail.com
* **Phone:** +91-6232625599
* **Education:** B.Tech in Information Technology, University Institute of Technology, RGPV
* **Portfolio:** [www.prakhar.life](http://www.prakhar.life)