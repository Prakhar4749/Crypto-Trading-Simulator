import React, { createContext, useContext, useReducer } from 'react'
import axiosInstance from '../config/axiosInstance'
import { showToast } from '@/utils/toast'

// 1. Initial State
const initialState = {
  asset: null,
  userAssets: [],
  assetDetails: null,
  loading: false,
  error: null
}

// 2. Reducer
function assetsReducer(state, action) {
  switch (action.type) {
    case 'ASSETS_REQUEST':
      return { ...state, loading: true, error: null }
    case 'ASSETS_SUCCESS':
      return { ...state, loading: false, ...action.payload }
    case 'ASSETS_FAILURE':
      return { ...state, loading: false, error: action.payload }
    default:
      return state
  }
}

// 3. Create Context
const AssetsContext = createContext()

// 4. Provider Component
export const AssetsProvider = ({ children }) => {
  const [state, dispatch] = useReducer(assetsReducer, initialState)

  const getUserAssets = async () => {
    dispatch({ type: 'ASSETS_REQUEST' })
    try {
      const response = await axiosInstance.get("/api/assets")
      const assets = response.data
      dispatch({ type: 'ASSETS_SUCCESS', payload: { userAssets: assets } })
      return assets
    } catch (error) {
      dispatch({ type: 'ASSETS_FAILURE', payload: error.message })
      throw error
    }
  }

  const getAssetDetails = async (assetId) => {
    dispatch({ type: 'ASSETS_REQUEST' })
    try {
      const response = await axiosInstance.get(`/api/assets/${assetId}`)
      dispatch({ type: 'ASSETS_SUCCESS', payload: { asset: response.data } })
      return response.data
    } catch (error) {
      dispatch({ type: 'ASSETS_FAILURE', payload: error.message })
      throw error
    }
  }

  const getAssetByUserIdAndCoinId = async (coinId) => {
    dispatch({ type: 'ASSETS_REQUEST' })
    try {
      const response = await axiosInstance.get(`/api/assets/coin/${coinId}`)
      dispatch({ type: 'ASSETS_SUCCESS', payload: { assetDetails: response.data } })
      return response.data
    } catch (error) {
      dispatch({ type: 'ASSETS_FAILURE', payload: error.message })
      throw error
    }
  }

  return (
    <AssetsContext.Provider value={{ ...state, getUserAssets, getAssetDetails, getAssetByUserIdAndCoinId }}>
      {children}
    </AssetsContext.Provider>
  )
}

// 5. Custom Hook
export const useAssets = () => {
  const context = useContext(AssetsContext)
  if (!context) throw new Error("useAssets must be used within AssetsProvider")
  return context
}

export default AssetsContext
