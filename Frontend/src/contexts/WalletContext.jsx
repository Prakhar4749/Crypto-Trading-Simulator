import React, { createContext, useContext, useReducer } from 'react'
import axiosInstance from '../config/axiosInstance'
import { showToast } from '@/utils/toast'

// 1. Initial State
const initialState = {
  userWallet: null,
  transactions: [],
  loading: false,
  error: null
}

// 2. Reducer
function walletReducer(state, action) {
  switch (action.type) {
    case 'WALLET_REQUEST':
      return { ...state, loading: true, error: null }
    case 'WALLET_SUCCESS':
      return { ...state, loading: false, ...action.payload }
    case 'WALLET_FAILURE':
      return { ...state, loading: false, error: action.payload }
    default:
      return state
  }
}

// 3. Create Context
const WalletContext = createContext()

// 4. Provider Component
export const WalletProvider = ({ children }) => {
  const [state, dispatch] = useReducer(walletReducer, initialState)

  const getUserWallet = async () => {
    dispatch({ type: 'WALLET_REQUEST' })
    try {
      const response = await axiosInstance.get("/api/wallet")
      dispatch({ type: 'WALLET_SUCCESS', payload: { userWallet: response.data } })
      return response.data
    } catch (error) {
      dispatch({ type: 'WALLET_FAILURE', payload: error.message })
      throw error
    }
  }

  const getWalletTransactions = async () => {
    dispatch({ type: 'WALLET_REQUEST' })
    try {
      const response = await axiosInstance.get("/api/wallet/transactions")
      dispatch({ type: 'WALLET_SUCCESS', payload: { transactions: response.data } })
      return response.data
    } catch (error) {
      dispatch({ type: 'WALLET_FAILURE', payload: error.message })
      throw error
    }
  }

  const topUpWallet = async ({ orderId, paymentId }) => {
    dispatch({ type: 'WALLET_REQUEST' })
    try {
      const response = await axiosInstance.put(`/api/wallet/deposit`, null, {
        params: {
          order_id: orderId,
          payment_id: paymentId,
        }
      })
      dispatch({ type: 'WALLET_SUCCESS', payload: { userWallet: response.data } })
      showToast.success("Wallet top-up successful")
      return response.data
    } catch (error) {
      dispatch({ type: 'WALLET_FAILURE', payload: error.message })
      showToast.fromError(error)
      throw error
    }
  }

  const transferMoney = async ({ walletId, amount, purpose }) => {
    dispatch({ type: 'WALLET_REQUEST' })
    try {
      const response = await axiosInstance.put(`/api/wallet/transfer`, {
        receiverWalletId: walletId,
        amount,
        purpose
      })
      dispatch({ type: 'WALLET_SUCCESS', payload: { userWallet: response.data } })
      showToast.success(`Successfully transferred $${amount}`)
      return response.data
    } catch (error) {
      dispatch({ type: 'WALLET_FAILURE', payload: error.message })
      showToast.fromError(error)
      throw error
    }
  }

  const paymentHandler = async (amount, paymentMethod) => {
    dispatch({ type: 'WALLET_REQUEST' })
    try {
      const response = await axiosInstance.post(
        `/api/payment/${paymentMethod}/amount/${amount}`
      )
      window.location.href = response.data.payment_url
      dispatch({ type: 'WALLET_SUCCESS', payload: { } })
      return response.data
    } catch (error) {
      dispatch({ type: 'WALLET_FAILURE', payload: error.message })
      showToast.fromError(error)
      throw error
    }
  }

  return (
    <WalletContext.Provider value={{ 
      ...state, 
      getUserWallet, 
      getWalletTransactions, 
      topUpWallet, 
      transferMoney,
      paymentHandler
    }}>
      {children}
    </WalletContext.Provider>
  )
}

// 5. Custom Hook
export const useWallet = () => {
  const context = useContext(WalletContext)
  if (!context) throw new Error("useWallet must be used within WalletProvider")
  return context
}

export default WalletContext
