# Backend Software Requirements Specification (SRS)
## Project Name: CoinDesk Simulator
**Date:** March 12, 2026  
**Document Version:** 5.0

---

## 1. Introduction

### 1.1 Purpose
This document defines the backend software requirements for the CoinDesk Simulator, a cryptocurrency trading simulation platform. The backend provides the core infrastructure for simulated crypto trading, virtual wallet management, real-time market data retrieval, and AI-driven market analysis.

### 1.2 Scope
The backend system operates on a distributed microservices architecture to ensure scalability and separation of concerns. It handles secure user authentication, inter-service communication, asynchronous event processing, and third-party API integrations (market data, AI, and payments).

---

## 2. System Architecture

### 2.1 Technology Stack
* **Core Framework:** Java (JDK 17+) with Spring Boot
* **Microservices Ecosystem:** Spring Cloud (Service Registry, API Gateway, Declarative REST Clients)
* **Message Broker:** Apache Kafka for asynchronous, event-driven communication
* **Data Access:** Spring Data JPA / Hibernate, with specialized database clients for complex queries
* **Database:** Relational Database (PostgreSQL) using a shared instance with logical data separation
* **Security:** Stateless JWT authentication and Role-Based Access Control (RBAC)
* **Integrations:** Payment Gateway (Razorpay), Market Data API (CoinGecko), AI Service (Google Gemini)

### 2.2 Microservices Overview
The backend is decomposed into the following specialized services:
1. **Service Registry:** Centralized discovery server for dynamic routing.
2. **API Gateway:** Single entry point handling centralized CORS, global JWT validation, and request routing.
3. **Auth Service:** Manages user identity, registration, login, Two-Factor Authentication (2FA), OAuth2, and password resets.
4. **Core Trading Service:** Manages virtual wallets, executes simulated buy/sell orders, tracks portfolio assets, handles watchlists, and processes withdrawal requests (including the Admin Panel logic).
5. **Market AI Service:** Acts as a proxy for external cryptocurrency market data and manages interactions with the AI chatbot.
6. **Notification Service:** An asynchronous listener that dispatches emails and alerts based on domain events.
7. **Shared Common Library:** Contains standardized Data Transfer Objects (DTOs), event payloads, and utility classes used across all services.

---

## 3. Functional Requirements

### 3.1 Authentication & User Management
* **FR1.1 Registration & Auth:** Support standard email/password registration and third-party OAuth2 logins.
* **FR1.2 Security:** Issue and validate stateless JWTs.
* **FR1.3 2FA & Verification:** Support OTP-based Two-Factor Authentication and email verification workflows.

### 3.2 Wallet & Financial Transactions
* **FR2.1 Automated Provisioning:** Automatically provision a virtual wallet and credit a simulated signup bonus upon account creation via asynchronous events.
* **FR2.2 Deposits:** Process user deposits through integrated payment gateways, converting real-world currency to simulated platform currency using a configured exchange rate.
* **FR2.3 Withdrawals:** Allow users to request withdrawals, manage payment methods, and view transaction history.

### 3.3 Trading & Portfolio
* **FR3.1 Order Execution:** Process simulated buy and sell orders against real-time market prices.
* **FR3.2 Asset Tracking:** Maintain accurate ledgers of user holdings, calculating average buy prices and portfolio values.
* **FR3.4 Watchlist:** Allow users to save and monitor specific cryptocurrencies.

### 3.4 Market Data & AI
* **FR4.1 Data Proxying:** Fetch and cache real-time and historical coin data from external providers.
* **FR4.2 AI Chatbot:** Process user prompts and return AI-generated market insights and conversational responses.

### 3.5 Administrator Operations
* **FR5.1 Withdrawal Management:** Provide secure endpoints for administrators to fetch, approve, or decline pending withdrawal requests.

---

## 4. Non-Functional Requirements

### 4.1 Security
* Global request filtering at the API Gateway level to validate tokens before routing.
* Internal traffic must be secured using internal API keys to prevent direct access to microservices bypassing the gateway.

### 4.2 Data Management
* Maintain a strict Entity-to-DTO mapping layer to prevent internal domain models from leaking to the API consumers.
* Utilize automated schema generation and database connection pooling optimized for cloud environments.