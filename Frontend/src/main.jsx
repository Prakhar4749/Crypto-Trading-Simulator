import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import './index.css'
import { BrowserRouter } from 'react-router-dom'
import { GoogleOAuthProvider } from '@react-oauth/google'
import ErrorBoundary from './components/ErrorBoundary'
import { ThemeProvider } from './contexts/ThemeContext'
import {
  AuthProvider,
  CoinProvider,
  WalletProvider,
  OrderProvider,
  AssetsProvider,
  WatchlistProvider,
  WithdrawalProvider,
  ChatProvider,
} from './contexts'

console.log("[Main] CoinDesk app mounted")
console.log("[Main] Running without Redux — Context API only")

const googleClientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;

if (!googleClientId) {
  console.warn("[Main] VITE_GOOGLE_CLIENT_ID is missing! Google Login will not work.");
}

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter>
      <GoogleOAuthProvider clientId={googleClientId}>
        <ErrorBoundary>
          <ThemeProvider>
            <AuthProvider>
              <CoinProvider>
                <WalletProvider>
                  <OrderProvider>
                    <AssetsProvider>
                      <WatchlistProvider>
                        <WithdrawalProvider>
                          <ChatProvider>
                            <App />
                          </ChatProvider>
                        </WithdrawalProvider>
                      </WatchlistProvider>
                    </AssetsProvider>
                  </OrderProvider>
                </WalletProvider>
              </CoinProvider>
            </AuthProvider>
          </ThemeProvider>
        </ErrorBoundary>
      </GoogleOAuthProvider>
    </BrowserRouter>
  </React.StrictMode>,
)
