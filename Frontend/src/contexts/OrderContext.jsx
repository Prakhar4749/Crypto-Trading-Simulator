import React, { createContext, useContext, useReducer } from 'react'
import axiosInstance from '../config/axiosInstance'
import { showToast } from '@/utils/toast'

// 1. Initial State
const initialState = {
  order: null,
  orders: [],
  loading: false,
  error: null
}

// 2. Reducer
function orderReducer(state, action) {
  switch (action.type) {
    case 'ORDER_REQUEST':
      return { ...state, loading: true, error: null }
    case 'ORDER_SUCCESS':
      return { ...state, loading: false, ...action.payload }
    case 'ORDER_FAILURE':
      return { ...state, loading: false, error: action.payload }
    default:
      return state
  }
}

// 3. Create Context
const OrderContext = createContext()

// 4. Provider Component
export const OrderProvider = ({ children }) => {
  const [state, dispatch] = useReducer(orderReducer, initialState)

  const payOrder = async (data) => {
    dispatch({ type: 'ORDER_REQUEST' })
    try {
      const response = await axiosInstance.post('/api/orders/pay', data)
      const order = response.data
      dispatch({ type: 'ORDER_SUCCESS', payload: { order } })
      showToast.success(`${data.orderType} order executed successfully`)
      return order
    } catch (error) {
      dispatch({ type: 'ORDER_FAILURE', payload: error.message })
      showToast.fromError(error)
      throw error
    }
  }

  const getAllOrdersOfUser = async () => {
    dispatch({ type: 'ORDER_REQUEST' })
    try {
      const response = await axiosInstance.get('/api/orders')
      const orders = response.data
      dispatch({ type: 'ORDER_SUCCESS', payload: { orders } })
      return orders
    } catch (error) {
      dispatch({ type: 'ORDER_FAILURE', payload: error.message })
      throw error
    }
  }

  return (
    <OrderContext.Provider value={{ ...state, payOrder, getAllOrdersOfUser }}>
      {children}
    </OrderContext.Provider>
  )
}

// 5. Custom Hook
export const useOrder = () => {
  const context = useContext(OrderContext)
  if (!context) throw new Error("useOrder must be used within OrderProvider")
  return context
}

export default OrderContext
