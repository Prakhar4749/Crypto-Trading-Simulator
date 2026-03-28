# Market AI Service API Documentation

**Base URL**: `http://localhost:8081`  

---

### 1. GET /api/coins
**Service**: market-ai-service  
**Description**: Get list of cryptocurrencies.  
**Auth Required**: Yes  

**Error Responses**:
| Status | Error Code | Scenario | Message |
|--------|------------|----------|---------|
| 422 | BUSINESS_RULE_VIOLATION | Invalid page | "Page number must be at least 1" |
| 503 | EXTERNAL_SERVICE_ERROR | CoinGecko down | "CoinGecko is currently unavailable" |

---

### 2. GET /api/coins/details/{coinId}
**Service**: market-ai-service  
**Description**: Get full details for a coin.  
**Auth Required**: Yes  

**Error Responses**:
| Status | Error Code | Scenario | Message |
|--------|------------|----------|---------|
| 404 | RESOURCE_NOT_FOUND | Invalid ID | "Coin not found: invalid-id" |
| 503 | EXTERNAL_SERVICE_ERROR | Rate limit | "Failed to fetch market data: 429 Too Many Requests" |

---

### 6. POST /api/chat/bot
**Service**: market-ai-service  
**Description**: AI chatbot.  
**Auth Required**: Yes  

#### Request
**Body**:
```json
{ "prompt": "..." }
```

**Error Responses**:
| Status | Error Code | Scenario | Message |
|--------|------------|----------|---------|
| 400 | VALIDATION_ERROR | Empty prompt | "Validation failed..." |
| 503 | EXTERNAL_SERVICE_ERROR | Gemini AI error | "AI service error: ..." |
| 503 | EXTERNAL_SERVICE_ERROR | Gemini down | "AI chatbot is temporarily unavailable" |
