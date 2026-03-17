# CoinDesk Simulator (In Development)

## 🚀 Introduction
Welcome to the repository for **CoinDesk Simulator**! This project is currently in the initial planning and architecture phase.

Once developed, CoinDesk Simulator will be a comprehensive, risk-free cryptocurrency trading platform. It is designed to mimic the experience of industry-leading exchanges, allowing users to practice crypto trading using virtual currency, monitor real-time market trends, manage their portfolios, and receive market insights from an integrated AI assistant.

## ✨ Planned Features
* **Risk-Free Trading:** Users will receive a simulated $10,000 signup bonus to practice executing buy and sell orders.
* **Real-Time Market Data:** Integration with external APIs to fetch and display live cryptocurrency prices, market caps, and historical charts.
* **AI Market Assistant:** A conversational AI chatbot will be available to analyze market trends and provide users with trading insights.
* **Virtual Wallet Management:** Users will be able to simulate deposits, request virtual withdrawals, and manage their payment methods.
* **Secure Authentication:** Robust user security featuring JWT-based authentication, Two-Factor Authentication (2FA), and OAuth2 integration.
* **Admin Dashboard:** A dedicated backend and interface for administrators to review and process user withdrawal requests.

## 🛠️ Proposed Tech Stack
The platform will be built using a modern, scalable distributed architecture:
* **Backend:** Java 17, Spring Boot 3.2.4 (Microservices Architecture)
* **Infrastructure:** Spring Cloud (Eureka, API Gateway, Feign Clients), Apache Kafka for event-driven processing
* **Database:** PostgreSQL (shared instance with logical separation) and Redis for caching
* **Frontend:** React.js / Vite (Planned)
* **External Integrations:** Google Gemini AI, CoinGecko API, Razorpay

## 🏗️ Architecture Overview
The backend will be broken down into specialized microservices to ensure high availability and separation of concerns:
1. `service-registry`
2. `api-gateway`
3. `auth-service`
4. `core-trading-service`
5. `market-ai-service`
6. `notification-service`

*Note: Detailed setup instructions, environment variable configurations, and deployment guides will be added to this README as the codebase is implemented.*

## 🤝 Contributing
Guidelines for contributing to the frontend and backend repositories will be established once the foundational scaffolding is complete.

---

## 👨‍💻 Developer
**Prakhar Sakhare**
* **Email:** prakharsakhare2226@gmail.com
* **Phone:** +91-6232625599
* **Portfolio:** [www.prakhar.life](http://www.prakhar.life)
* **Education:** B.Tech in Information Technology, University Institute of Technology, RGPV