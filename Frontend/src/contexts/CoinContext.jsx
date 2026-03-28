import React, { createContext, useContext, useReducer } from 'react'
import axiosInstance from '../config/axiosInstance'
import { showToast } from '@/utils/toast'

// 1. Initial State
const initialState = {
  coinList: [],
  top50: [],
  searchCoinList: [],
  marketChart: { data: [], loading: false },
  coinById: null,
  coinDetails: null,
  loading: false,
  error: null
}

// 2. Reducer
function coinReducer(state, action) {
  switch (action.type) {
    case 'COIN_REQUEST':
      return { ...state, loading: true, error: null }
    case 'COIN_SUCCESS':
      return { ...state, loading: false, ...action.payload }
    case 'COIN_FAILURE':
      return { ...state, loading: false, error: action.payload }
    case 'MARKET_CHART_REQUEST':
      return { ...state, marketChart: { data: [], loading: true } }
    case 'MARKET_CHART_SUCCESS':
      return { ...state, marketChart: { data: action.payload, loading: false } }
    case 'MARKET_CHART_FAILURE':
      return { ...state, marketChart: { data: [], loading: false }, error: action.payload }
    default:
      return state
  }
}

// 3. Create Context
const CoinContext = createContext()

// 4. Provider Component
export const CoinProvider = ({ children }) => {
  const [state, dispatch] = useReducer(coinReducer, initialState)

  const getCoinList = async (page) => {
    dispatch({ type: 'COIN_REQUEST' })
    try {
      const response = await axiosInstance.get(`/api/coins?page=${page}`)
      dispatch({ type: 'COIN_SUCCESS', payload: { coinList: response.data } })
      return response.data
    } catch (error) {
      dispatch({ type: 'COIN_FAILURE', payload: error.message })
      throw error
    }
  }

  const getTop50Coins = async () => {
    dispatch({ type: 'COIN_REQUEST' })
    try {
      const response = await axiosInstance.get(`/api/coins/top50`)
      dispatch({ type: 'COIN_SUCCESS', payload: { top50: response.data } })
      return response.data
    } catch (error) {
      dispatch({ type: 'COIN_FAILURE', payload: error.message })
      throw error
    }
  }

  const getTrendingCoins = async () => {
    dispatch({ type: 'COIN_REQUEST' })
    try {
      const response = await axiosInstance.get(`/api/coins/trading`)
      dispatch({ type: 'COIN_SUCCESS', payload: { top50: response.data } })
      return response.data
    } catch (error) {
      dispatch({ type: 'COIN_FAILURE', payload: error.message })
      throw error
    }
  }

  const fetchCoinDetails = async ({ coinId }) => {
    dispatch({ type: 'COIN_REQUEST' })
    try {
      const response = await axiosInstance.get(`/api/coins/${coinId}`)
      dispatch({ type: 'COIN_SUCCESS', payload: { coinDetails: response.data } })
      return response.data
    } catch (error) {
      dispatch({ type: 'COIN_FAILURE', payload: error.message })
      throw error
    }
  }

  const searchCoins = async (query) => {
    dispatch({ type: 'COIN_REQUEST' })
    try {
      const response = await axiosInstance.get(`/api/coins/search?q=${query}`)
      dispatch({ type: 'COIN_SUCCESS', payload: { searchCoinList: response.data.coins } })
      return response.data
    } catch (error) {
      dispatch({ type: 'COIN_FAILURE', payload: error.message })
      throw error
    }
  }

  const getCoinById = async (coinId) => {
    dispatch({ type: 'COIN_REQUEST' })
    try {
      const response = await axiosInstance.get(`/api/coins/${coinId}`)
      dispatch({ type: 'COIN_SUCCESS', payload: { coinById: response.data, coinDetails: response.data } })
      return response.data
    } catch (error) {
      dispatch({ type: 'COIN_FAILURE', payload: error.message })
      throw error
    }
  }

  const getCoinMarketChart = async (coinId, days) => {
    dispatch({ type: 'MARKET_CHART_REQUEST' })
    try {
      const response = await axiosInstance.get(`/api/coins/${coinId}/chart?days=${days}`)
      dispatch({ type: 'MARKET_CHART_SUCCESS', payload: response.data.prices })
      return response.data
    } catch (error) {
      dispatch({ type: 'MARKET_CHART_FAILURE', payload: error.message })
      throw error
    }
  }

  const fetchMarketData = async (coinId) => {
    try {
      const response = await axiosInstance.get(`/api/coins/${coinId}`)
      return response.data
    } catch (error) {
      showToast.fromError(error)
      throw error
    }
  }

  return (
    <CoinContext.Provider value={{ 
      ...state, 
      getCoinList, 
      getTop50Coins,
      getTrendingCoins,
      fetchCoinDetails,
      searchCoins, 
      getCoinById, 
      getCoinMarketChart, 
      fetchMarketData 
    }}>
      {children}
    </CoinContext.Provider>
  )
}

// 5. Custom Hook
export const useCoins = () => {
  const context = useContext(CoinContext)
  if (!context) throw new Error("useCoins must be used within CoinProvider")
  return context
}

export default CoinContext
