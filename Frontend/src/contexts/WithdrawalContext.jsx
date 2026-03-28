import React, { createContext, useContext, useReducer } from 'react'
import axiosInstance from '../config/axiosInstance'
import { showToast } from '@/utils/toast'

// 1. Initial State
const initialState = {
  withdrawal: null,
  history: [],
  paymentDetails: null,
  requests: [],
  loading: false,
  error: null
}

// 2. Reducer
function withdrawalReducer(state, action) {
  switch (action.type) {
    case 'WITHDRAWAL_REQUEST':
      return { ...state, loading: true, error: null }
    case 'WITHDRAWAL_SUCCESS':
      return { ...state, loading: false, ...action.payload }
    case 'WITHDRAWAL_FAILURE':
      return { ...state, loading: false, error: action.payload }
    default:
      return state
  }
}

// 3. Create Context
const WithdrawalContext = createContext()

// 4. Provider Component
export const WithdrawalProvider = ({ children }) => {
  const [state, dispatch] = useReducer(withdrawalReducer, initialState)

  const withdrawalRequest = async (amount) => {
    dispatch({ type: 'WITHDRAWAL_REQUEST' })
    try {
      const response = await axiosInstance.post(`/api/withdrawal/${amount}`)
      const withdrawal = response.data
      dispatch({ type: 'WITHDRAWAL_SUCCESS', payload: { withdrawal } })
      showToast.success(`Withdrawal request for $${amount} submitted`)
      return withdrawal
    } catch (error) {
      dispatch({ type: 'WITHDRAWAL_FAILURE', payload: error.message })
      showToast.fromError(error)
      throw error
    }
  }

  const getWithdrawalHistory = async () => {
    dispatch({ type: 'WITHDRAWAL_REQUEST' })
    try {
      const response = await axiosInstance.get('/api/withdrawal')
      const history = response.data
      dispatch({ type: 'WITHDRAWAL_SUCCESS', payload: { history } })
      return history
    } catch (error) {
      dispatch({ type: 'WITHDRAWAL_FAILURE', payload: error.message })
      throw error
    }
  }

  const getAllWithdrawalRequests = async () => {
    dispatch({ type: 'WITHDRAWAL_REQUEST' })
    try {
      const response = await axiosInstance.get('/api/admin/withdrawal')
      const requests = response.data
      dispatch({ type: 'WITHDRAWAL_SUCCESS', payload: { requests } })
      return requests
    } catch (error) {
      dispatch({ type: 'WITHDRAWAL_FAILURE', payload: error.message })
      throw error
    }
  }

  const updateWithdrawalStatus = async (id, status) => {
    dispatch({ type: 'WITHDRAWAL_REQUEST' })
    try {
      const response = await axiosInstance.patch(`/api/admin/withdrawal/${id}/proceed/${status}`)
      const updatedRequest = response.data
      
      const updatedRequests = state.requests.map(req => req.id === updatedRequest.id ? updatedRequest : req)
      dispatch({ type: 'WITHDRAWAL_SUCCESS', payload: { requests: updatedRequests } })
      showToast.success(`Withdrawal ${status ? 'approved' : 'declined'}`)
      return updatedRequest
    } catch (error) {
      dispatch({ type: 'WITHDRAWAL_FAILURE', payload: error.message })
      showToast.fromError(error)
      throw error
    }
  }

  const addPaymentDetails = async (paymentDetails) => {
    dispatch({ type: 'WITHDRAWAL_REQUEST' })
    try {
      const response = await axiosInstance.post(`/api/payment-details`, paymentDetails)
      dispatch({ type: 'WITHDRAWAL_SUCCESS', payload: { paymentDetails: response.data } })
      showToast.success("Payment details saved successfully")
      return response.data
    } catch (error) {
      dispatch({ type: 'WITHDRAWAL_FAILURE', payload: error.message })
      showToast.fromError(error)
      throw error
    }
  }

  const getPaymentDetails = async () => {
    dispatch({ type: 'WITHDRAWAL_REQUEST' })
    try {
      const response = await axiosInstance.get(`/api/payment-details`)
      dispatch({ type: 'WITHDRAWAL_SUCCESS', payload: { paymentDetails: response.data } })
      return response.data
    } catch (error) {
      dispatch({ type: 'WITHDRAWAL_FAILURE', payload: error.message })
      throw error
    }
  }

  return (
    <WithdrawalContext.Provider value={{ 
      ...state, 
      withdrawalRequest, 
      getWithdrawalHistory, 
      getAllWithdrawalRequests, 
      updateWithdrawalStatus,
      addPaymentDetails,
      getPaymentDetails
    }}>
      {children}
    </WithdrawalContext.Provider>
  )
}

// 5. Custom Hook
export const useWithdrawal = () => {
  const context = useContext(WithdrawalContext)
  if (!context) throw new Error("useWithdrawal must be used within WithdrawalProvider")
  return context
}

export default WithdrawalContext
