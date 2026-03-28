# Core Trading Service API Documentation

**Base URL**: `http://localhost:8081`  

---

### 1. GET /api/wallet
**Service**: core-trading-service  
**Description**: Get current user's wallet balance.  
**Auth Required**: Yes  

#### Response
**Success (200)**:
```json
{
  "success": true,
  "message": "Wallet fetched successfully",
  "data": { "balance": 10000.0 }
}
```

**Error Responses**:
| Status | Error Code | Scenario | Message |
|--------|------------|----------|---------|
| 401 | UNAUTHORIZED | No/Invalid token | "Unauthorized" |
| 404 | RESOURCE_NOT_FOUND | Wallet doesn't exist | "Wallet not found: userId=1" |

---

### 3. PUT /api/wallet/transfer
**Service**: core-trading-service  
**Description**: Transfer simulator USD to another user's wallet.  
**Auth Required**: Yes  

#### Request
**Body**:
```json
{
  "receiverWalletId": 2,
  "amount": 500,
  "purpose": "Gift"
}
```

#### Response
**Success (200)**:
```json
{
  "success": true,
  "message": "Transfer successful",
  "data": { "balance": 9500.0 }
}
```

**Error Responses**:
| Status | Error Code | Scenario | Message |
|--------|------------|----------|---------|
| 422 | INSUFFICIENT_BALANCE | Low balance | "Insufficient balance. Required: $500, Available: $100" |
| 422 | BUSINESS_RULE_VIOLATION | Self transfer | "Cannot transfer to your own wallet" |
| 404 | RESOURCE_NOT_FOUND | Invalid receiver | "Wallet not found: id=2" |

---

### 5. POST /api/orders/pay
**Service**: core-trading-service  
**Description**: Execute a BUY or SELL trade.  
**Auth Required**: Yes  

#### Request
**Body**:
```json
{
  "coinId": "bitcoin",
  "quantity": 0.1,
  "orderType": "BUY"
}
```

#### Response
**Success (200)**:
```json
{
  "success": true,
  "message": "Trade processed successfully",
  "data": null
}
```

**Error Responses**:
| Status | Error Code | Scenario | Message |
|--------|------------|----------|---------|
| 422 | INSUFFICIENT_BALANCE | Low balance for BUY | "Insufficient balance..." |
| 422 | BUSINESS_RULE_VIOLATION | No holdings for SELL | "You don't own any bitcoin to sell" |
| 503 | EXTERNAL_SERVICE_ERROR | Market service down | "market-ai-service is unavailable..." |

---

### 9. POST /api/withdrawal/{amount}
**Service**: core-trading-service  
**Description**: Request a withdrawal.  
**Auth Required**: Yes  

**Error Responses**:
| Status | Error Code | Scenario | Message |
|--------|------------|----------|---------|
| 422 | BUSINESS_RULE_VIOLATION | No bank details | "Please add bank/UPI payment details..." |
| 422 | BUSINESS_RULE_VIOLATION | Pending request exists | "You already have a pending withdrawal request..." |
| 422 | INSUFFICIENT_BALANCE | Amount > balance | "Insufficient balance..." |
