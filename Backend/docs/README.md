# Trading Simulator API Documentation

## 📁 Files
| File | Description |
|------|-------------|
| [01-auth-service-api.md](./api/01-auth-service-api.md) | Auth, login, signup, 2FA |
| [02-core-trading-api.md](./api/02-core-trading-api.md) | Wallet, orders, assets, withdrawals |
| [03-market-ai-api.md](./api/03-market-ai-api.md) | Coins, market, AI chat |
| [Postman Collection](./api/TradingSimulator.postman_collection.json) | Import to Postman |
| [Postman Environment](./api/TradingSimulator.postman_environment.json) | Postman env vars |

## 🚀 Quick Start Testing

### Step 1 — Import to Postman
1. Open Postman
2. Click **Import**
3. Select `TradingSimulator.postman_collection.json`
4. Also import `TradingSimulator.postman_environment.json`
5. Select the **Trading Simulator Local** environment from top-right dropdown

### Step 2 — Start Backend Services
Start in this order:
1. service-registry (port 8761)
2. auth-service (port 5454)
3. core-trading-service (port 5456)
4. market-ai-service (port 5455)
5. notification-service (port 5460)
6. api-gateway (port 8081)

### Step 3 — First API Test
1. Run **Signup User** request in Auth folder
2. JWT token auto-saves to collection variables
3. Run any protected endpoint — token auto-attached

## 💰 Simulator Currency Notes
- All balances are in virtual USD ($)
- New user gets **$10,000 free** on signup
- Deposit: Pay INR → get USD × 10,000 (via webhook)
- Example: ₹100 INR = $1,000,000 simulator USD

## 🔒 Authentication
- **Public endpoints**: `/auth/signup`, `/auth/signin`, `/auth/google`
- **Protected endpoints**: Need `Authorization: Bearer <token>`
- Token is auto-saved after login/signup in Postman

## 👑 Admin Access
- Use admin credentials from `.env`
- Login as admin to get admin JWT token
- Set `admin_token` collection variable manually for admin endpoints
