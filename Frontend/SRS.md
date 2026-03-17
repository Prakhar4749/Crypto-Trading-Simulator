# Frontend Software Requirements Specification (SRS)
## Project Name: CoinDesk Simulator
**Date:** March 12, 2026  
**Document Version:** 5.0

---

## 1. Introduction

### 1.1 Purpose
This document defines the frontend software requirements for the CoinDesk Simulator. The frontend is a responsive, web-based user interface that allows users to interact with the simulated cryptocurrency trading platform, view market trends, manage their virtual portfolios, and chat with an AI market assistant.

### 1.2 Scope
The frontend application interfaces with the backend microservices via the central API Gateway. It is responsible for state management, secure token storage, real-time data visualization, and providing an intuitive, seamless user experience similar to industry-standard cryptocurrency exchanges.

---

## 2. System Architecture

### 2.1 Proposed Technology Stack
* **Core Framework:** React.js (or similar modern component-based UI framework)
* **Build Tool:** Vite (or equivalent fast bundler)
* **Styling:** CSS Framework (e.g., Tailwind CSS) for responsive design
* **State Management:** Context API or Redux for global application state
* **Routing:** Client-side routing for seamless navigation
* **Data Fetching:** Axios or Fetch API with request interceptors for JWT injection

### 2.2 Core Modules
1. **Public Module:** Landing page, Authentication (Login/Signup), and Password Recovery flows.
2. **Market Module:** Real-time coin listings, detailed chart views, and watchlists.
3. **Trading & Portfolio Module:** Order execution interfaces (Buy/Sell), asset tracking tables, and wallet management (Deposits/Withdrawals).
4. **AI Assistant Module:** Chat interface for interacting with the market analysis bot.
5. **Admin Module:** Dashboard for platform administrators to manage user withdrawal requests.

---

## 3. Functional Requirements

### 3.1 Authentication & Profile
* **FR1.1 Forms:** Provide user-friendly forms for registration, login, and OTP verification (2FA).
* **FR1.2 Session Management:** Securely store JWTs and handle session expirations gracefully, redirecting to login when necessary.
* **FR1.3 Profile Management:** Allow users to view their details and link bank/UPI payment information.

### 3.2 Market Exploration
* **FR2.1 Coin Listing:** Display a paginated or infinitely scrolling list of cryptocurrencies with current prices, market caps, and 24h changes.
* **FR2.2 Data Visualization:** Integrate charting libraries to display historical price data visually.
* **FR2.3 Watchlist UI:** Provide toggle buttons to add/remove coins to a personalized watchlist.

### 3.3 Trading Interface
* **FR3.1 Order Forms:** Provide intuitive input fields for executing market trades, displaying estimated costs in virtual currency before execution.
* **FR3.2 Portfolio Dashboard:** Present a dashboard summarizing total virtual balance, current asset valuations, and profit/loss metrics.

### 3.4 Wallet & Payments
* **FR4.1 Deposit Flow:** Integrate the Razorpay checkout UI to allow users to add funds, displaying the converted virtual currency amount clearly.
* **FR4.2 Withdrawal UI:** Provide a form for requesting withdrawals and a table detailing the status of past requests (Pending/Approved/Declined).

### 3.5 AI Chat Interface
* **FR5.1 Chat window:** Implement a conversational UI mimicking modern messaging apps, displaying user prompts and AI responses with loading indicators.

### 3.6 Admin Panel
* **FR6.1 Withdrawal Dashboard:** Display a secure table of pending user withdrawals.
* **FR6.2 Action Controls:** Provide UI buttons for administrators to quickly approve or reject pending requests.

---

## 4. Non-Functional Requirements

### 4.1 Performance & Responsiveness
* The interface must be fully responsive, functioning seamlessly on desktop, tablet, and mobile devices.
* Optimize asset loading and utilize lazy loading for distinct application modules to ensure fast initial render times.

### 4.2 Error Handling
* Provide clear, user-friendly toast notifications or alerts for API errors, successful actions, and network disconnections.